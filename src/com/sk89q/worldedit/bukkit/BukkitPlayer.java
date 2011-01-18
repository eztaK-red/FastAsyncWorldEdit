// $Id$
/*
 * WorldEdit
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.worldedit.bukkit;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BlockType;

public class BukkitPlayer extends LocalPlayer {
    private Player player;
    private WorldEditPlugin plugin;
    
    public BukkitPlayer(WorldEditPlugin plugin, ServerInterface server, Player player) {
        super(server);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public WorldVector getBlockTrace(int range) {
        TargetBlock tb = new TargetBlock(player, range, 0.2);
        Block block = tb.getTargetBlock();
        if (block == null) {
            return null;
        }
        return new WorldVector(getWorld(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    public WorldVector getSolidBlockTrace(int range) {
        TargetBlock tb = new TargetBlock(player, range, 0.2);
        tb.reset();
        while (tb.getNextBlock() != null
                && BlockType.canPassThrough(tb.getCurrentBlock().getTypeId()));
        Block block = tb.getCurrentBlock();
        if (block == null) {
            return null;
        }
        return new WorldVector(getWorld(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    public int getItemInHand() {
        ItemStack itemStack = player.getItemInHand();
        return itemStack != null ? itemStack.getTypeId() : 0;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public WorldVector getPosition() {
        Location loc = player.getLocation();
        return new WorldVector(new BukkitWorld(loc.getWorld()),
                loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public double getPitch() {
        return player.getLocation().getPitch();
    }

    @Override
    public double getYaw() {
        return player.getLocation().getYaw();
    }

    @Override
    public void giveItem(int type, int amt) {
        player.getWorld().dropItem(player.getLocation(), new ItemStack(type, amt));
        // TODO: Make this actually give the item
    }

    @Override
    public boolean passThroughForwardWall(int range) {
        boolean foundNext = false;
        int searchDist = 0;
        TargetBlock hitBlox = new TargetBlock(player, range, 0.2);
        LocalWorld world = getPosition().getWorld();
        Block block;
        while ((block = hitBlox.getNextBlock()) != null) {
            searchDist++;
            if (searchDist > 20) {
                return false;
            }
            if (BlockType.canPassThrough(block.getTypeId())) {
                if (foundNext) {
                    Vector v = new Vector(block.getX(), block.getY() - 1, block.getZ());
                    if (BlockType.canPassThrough(world.getBlockType(v))) {
                        setPosition(v.add(0.5, 0, 0.5));
                        return true;
                    }
                }
            } else {
                foundNext = true;
            }
        }
        return false;
    }

    @Override
    public void printRaw(String msg) {
        player.sendMessage(msg);
    }

    @Override
    public void print(String msg) {
        player.sendMessage("\u00A7d" + msg);
    }

    @Override
    public void printError(String msg) {
        player.sendMessage("\u00A7c" + msg);
    }

    @Override
    public void setPosition(Vector pos, float pitch, float yaw) {
        player.teleportTo(new Location(player.getWorld(), pos.getX(), pos.getY(),
                pos.getZ(), yaw, pitch));
    }

    @Override
    public String[] getGroups() {
        return plugin.getGroups(player);
    }

    @Override
    public BlockBag getInventoryBlockBag() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasPermission(String perm) {
        return plugin.hasPermission(player, "/" + perm);
    }

    @Override
    public LocalWorld getWorld() {
        return new BukkitWorld(player.getWorld());
    }

}
