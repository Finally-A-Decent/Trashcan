import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
    librariesFileName = "dumpster-libraries.json"
}

dependencies {
    api(project(":common"))
    compileOnlyApi(libs.dumpster.gson)
}

tasks {
    withType<ShadowJar> {
        dependsOn(":common:shadowJar")
    }
}

fun DependencyHandler.compileOnlyApiLibrary(dependencyNotation: Any): Dependency? =
    add("compileOnlyApiLibrary", dependencyNotation)