import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.grgit)
    alias(libs.plugins.shadow)
}

val devMode = grgit.branch.current().name != "master" && grgit.branch.current().name != "HEAD"

allprojects {
    group = "info.preva1l.trashcan"
    version = "1.1.0"

    repositories {
        mavenCentral()

        if (devMode) configureFinallyADecentRepository(devMode)
        configureFinallyADecentRepository()
    }
}

val parentProjects = arrayOf("fabric")

subprojects {
    if (parentProjects.contains(project.name)) return@subprojects

    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)

    if (parentProjects.contains(project.parent?.name)) {
        version = version as String + "-${project.name}"

        if (project.parent?.name?.equals("fabric") == true) {
            //apply(plugin = "fabric-loom")
        }
    }

    tasks {
        withType<ShadowJar> {
            exclude("META-INF/maven/**", "org/**", "paper-plugin.yml")
            archiveClassifier.set("")
        }

        withType<JavaCompile> {
            options.compilerArgs.add("-parameters")
            options.isFork = true
            options.encoding = "UTF-8"
        }

        register<Jar>("sourcesJar") {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        withType<Javadoc> {
            (options as StandardJavadocDocletOptions).tags(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:"
            )
        }

        register<Jar>("javadocJar") {
            dependsOn("javadoc")
            archiveClassifier.set("javadoc")
            from(named<Javadoc>("javadoc").get().destinationDir)
        }

        named("build") {
            dependsOn("shadowJar")
        }
    }

    publishing {
        repositories {
            configureFinallyADecentRepository(dev = devMode)
        }
        publications {
            register<MavenPublication>("mavenJava") {
                from(components["java"])
                artifact(tasks.named("sourcesJar"))
                artifact(tasks.named("javadocJar"))
            }
        }
    }
}

fun RepositoryHandler.configureFinallyADecentRepository(dev: Boolean = false) {
    val user = properties["fad_username"]?.toString() ?: System.getenv("fad_username")
    val pass = properties["fad_password"]?.toString() ?: System.getenv("fad_password")

    maven("https://repo.preva1l.info/${if (dev) "development" else "releases"}/") {
        name = "FinallyADecent"
        if (user != null && pass != null) {
            credentials {
                username = user
                password = pass
            }
        }
    }
}

logger.lifecycle("Building Trashcan $version")