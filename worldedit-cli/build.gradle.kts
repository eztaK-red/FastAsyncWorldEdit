import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
}

project.description = "CLI"

applyPlatformAndCoreConfiguration()
applyShadowConfiguration()

addJarManifest(WorldEditKind.Standalone("com.sk89q.worldedit.cli.CLIWorldEdit"))

dependencies {
    compileOnly(project(":worldedit-libs:core:ap"))
    annotationProcessor(project(":worldedit-libs:core:ap"))
    annotationProcessor("com.google.guava:guava:${Versions.GUAVA}")
    api(project(":worldedit-core"))
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.1") {
        because("We control Log4J on this platform")
    })
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("commons-cli:commons-cli:1.4")
    implementation("com.google.guava:guava")
    implementation("com.google.code.gson:gson")
}

tasks.named<ShadowJar>("shadowJar") {
    dependencies {
        include { true }
    }
    archiveFileName.set("${rootProject.name}-${project.description}-${project.version}.${archiveExtension.getOrElse("jar")}")
    minimize {
        exclude(dependency("org.apache.logging.log4j:log4j-core"))
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}
