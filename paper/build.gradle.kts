import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.GeneratePluginDescription

plugins {
    alias(libs.plugins.pluginyml.paper)
}

val compileOnlyApiLibrary = configurations.maybeCreate("compileOnlyApiLibrary")

plugins.withType<JavaPlugin> {
    extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
        configurations.compileOnlyApi.get().extendsFrom(compileOnlyApiLibrary)
        configurations.library.get().extendsFrom(compileOnlyApiLibrary)
    }
}

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    api(project(":common"))

    compileOnly(libs.bundles.paper.provided)

    compileOnlyApiLibrary(libs.bundles.paper.loaded)
    compileOnlyApiLibrary(libs.bundles.common.loaded)

    api(libs.bundles.paper.included)


    testImplementation(libs.bundles.paper.loaded)
    testImplementation(libs.bundles.common.loaded)
    testImplementation(libs.bundles.paper.included)
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.93.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

paper {
    main = "Trashcan ftw!"
    apiVersion = "1.19"
    generateLibrariesJson = true
}

tasks {
    withType<GeneratePluginDescription> {
        librariesJsonFileName = "trashcan-libraries.json"
    }

    withType<ShadowJar> {
        dependsOn(":common:shadowJar")
    }

    withType<Test> {
        dependsOn(":common:shadowJar")
        useJUnitPlatform()
    }
}

fun DependencyHandler.compileOnlyApiLibrary(dependencyNotation: Any): Dependency? =
    add("compileOnlyApiLibrary", dependencyNotation)