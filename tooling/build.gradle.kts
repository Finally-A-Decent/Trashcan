import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradle.plugin-publish") version "1.3.1"
    `kotlin-dsl`
    alias(libs.plugins.shadow)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.shadow)
    implementation(libs.grgit)
    implementation(libs.gson)
}

gradlePlugin {
    plugins {
        website = "https://docs.preva1l.info/"
        vcsUrl = "https://github.com/Finally-A-Decent/Trashcan.git"

        create("trashcan") {
            id = "info.preva1l.trashcan"
            implementationClass = "info.preva1l.trashcan.TrashcanPlugin"

            displayName = "Trashcan"
            description = "Build tooling to help with the Trashcan library and other general tasks."
            tags = listOf("minecraft", "bukkit", "paper", "fabric", "library", "tooling")
        }
    }
}

tasks {
    withType<ShadowJar> {
        exclude("META-INF/maven/**", "org/**", "paper-plugin.yml")
        archiveClassifier.set("")
    }

    withType<Javadoc> {
        (options as StandardJavadocDocletOptions).tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}