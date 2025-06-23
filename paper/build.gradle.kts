import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import info.preva1l.trashcan.paper

plugins {
    alias(libs.plugins.trashcan)
}

val compileOnlyApiLibrary = configurations.maybeCreate("compileOnlyApiLibrary")

plugins.withType<JavaPlugin> {
    extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
        configurations.compileOnlyApi.get().extendsFrom(compileOnlyApiLibrary)
        configurations.library.get().extendsFrom(compileOnlyApiLibrary)
    }
}

trashcan {
    librariesFileName = "trashcan-libraries.json"
}

dependencies {
    api(project(":common"))

    paper(libs.versions.paper.api.get())

    compileOnlyApiLibrary(libs.bundles.paper.loaded)
    compileOnlyApiLibrary(libs.bundles.common.loaded)

    api(libs.bundles.paper.included)


    testImplementation(libs.bundles.paper.loaded)
    testImplementation(libs.bundles.common.loaded)
    testImplementation(libs.bundles.paper.included)
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.93.2")
    testImplementation(platform("org.junit:junit-bom:5.13.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    withType<ShadowJar> {
        dependsOn(":common:shadowJar")
        mergeServiceFiles()
    }

    withType<Test> {
        dependsOn(":common:shadowJar")
        useJUnitPlatform()
    }
}

fun DependencyHandler.compileOnlyApiLibrary(dependencyNotation: Any): Dependency? =
    add("compileOnlyApiLibrary", dependencyNotation)