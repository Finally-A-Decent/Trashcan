package info.preva1l.trashcan

import info.preva1l.trashcan.tasks.GenerateLibrariesFileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*

/**
 * Created on 7/06/2025
 *
 * @author Preva1l
 */
class TrashcanPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val configuration = project.extensions.create(
            "trashcan",
            TrashcanExtension::class.java,
            project.objects
        )

        // dependency helpers
        project.dependencies.extensions.create(
            "trashcan",
            TrashcanDependencyExtension::class,
            project.dependencies,
            project.repositories,
            configuration
        )

        applyLibraryHelpers(project)
    }

    private fun applyLibraryHelpers(project: Project) {
        val generatedResourcesDirectory = project.layout.buildDirectory.dir("generated/trashcan/libraries")

        val library = project.configurations.maybeCreate("library")

        val generateTask = project.tasks.register<GenerateLibrariesFileTask>("generateLibrariesFile") {
            outputDirectory.set(generatedResourcesDirectory)
        }

        project.plugins.withType<JavaPlugin> {
            project.extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
                resources.srcDir(generateTask)
                project.configurations.getByName(compileOnlyConfigurationName).extendsFrom(library)
            }
        }
    }
}