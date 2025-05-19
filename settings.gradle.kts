pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.minecraftforge.net")
        maven(url = "https://repo.essential.gg/repository/maven-public/")
    }

//    plugins {
//        val egtVersion = "0.6.7"
//        id("gg.essential.defaults") version egtVersion
//        id("gg.essential.multi-version.root") version egtVersion
//    }
}

rootProject.name = "Trashcan"

include(
    "dumpster-common",
    "dumpster-hikari", "dumpster-sqlite",
    "dumpster-mongo"
)
include("common", "paper")
include("common-kotlin", "paper-kotlin")


//include("fabric")
//project(":fabric").projectDir = file("fabric/")
//project(":fabric").buildFileName = "root.gradle.kts"
//
//
//rootDir.resolve("fabric")
//    .listFiles { file -> file.isDirectory && file.name.matches(Regex("""\d+\.\d+(\.\d+)?""")) }
//    ?.forEach { dir ->
//        include("fabric:${dir.name}")
//        project(":fabric:${dir.name}").apply {
//            projectDir = file("fabric/${dir.name}")
//            buildFileName = "../build.gradle.kts"
//        }
//    }