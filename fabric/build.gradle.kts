plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

repositories {
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://maven.nucleoid.xyz")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.name}")
    mappings(essential.defaults.loom.mappings)
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${fabric_version}")

    implementation(include(project(":common")))
//    project(":common").configurations {
//        api.dependencies.each {include(it)}
//    }
}

processResources {
    filesMatching(listOf("fabric.mod.json", "compatibility.yml")) {
        expand(
            mapOf(
                "version" to rootProject.version,
                "loader_version" to properties["loader_version"],
                "minecraft_version" to project.name
            )
        )
    }
}

shadowJar {
    configurations = [project.configurations.shadow]
    destinationDirectory.set(file("$projectDir/build/libs"))

    exclude("/mappings/*")
}

tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    dependsOn(tasks.shadowJar)
    mustRunAfter(tasks.shadowJar)

    inputFile.set(tasks.shadowJar.get().archiveFile.get())
    addNestedDependencies.set(true)

    destinationDirectory.set(rootDir.resolve("target"))
    archiveClassifier.set("")
}

shadowJar.finalizedBy(remapJar)