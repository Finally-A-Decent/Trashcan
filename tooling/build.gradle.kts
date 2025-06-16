plugins {
    alias(libs.plugins.gradle)
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.grgit)
    implementation(libs.gson)
    implementation(libs.paperweight)
    implementation(libs.bundles.jackson) {
        exclude(group = "org.jetbrains.kotlin")
    }
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
        val user = properties["fad_username"]?.toString() ?: System.getenv("fad_username")
        val pass = properties["fad_password"]?.toString() ?: System.getenv("fad_password")

        maven("https://repo.preva1l.info/releases/") {
            name = "FinallyADecent"
            if (user != null && pass != null) {
                credentials {
                    username = user
                    password = pass
                }
            }
        }
    }
}