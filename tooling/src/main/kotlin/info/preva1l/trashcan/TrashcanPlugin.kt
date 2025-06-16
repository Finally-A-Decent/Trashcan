package info.preva1l.trashcan

import info.preva1l.trashcan.description.paper.PaperPluginDescription
import info.preva1l.trashcan.tasks.GenerateDescriptionFileTask
import info.preva1l.trashcan.tasks.GenerateLibrariesFileTask
import io.papermc.paperweight.userdev.PaperweightUser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*

/**
 * Created on 7/06/2025
 *
 * @author Preva1l
 */
class TrashcanPlugin : Plugin<Project> {
    private lateinit var generatedResourcesDirectory: Provider<out Directory>

    override fun apply(project: Project) {
        generatedResourcesDirectory = project.layout.buildDirectory.dir("generated/trashcan")

        val configuration = project.extensions.create(
            "trashcan",
            TrashcanExtension::class,
            project.objects
        )

        // dependency helpers
        project.dependencies.extensions.create(
            "trashcan",
            TrashcanDependencyExtension::class,
            project.dependencies,
            project.repositories,
            configuration,
            project.extensions
        )

        if (configuration.paper.get()) {
            project.plugins.apply(PaperweightUser::class)
        }

        project.extensions.add("paper", PaperPluginDescription(project))

        registerTasks(project)
    }

    private fun registerTasks(project: Project) {
        val library = project.configurations.maybeCreate("library")

        val generateLibrariesFileTask = project.tasks
            .register<GenerateLibrariesFileTask>("generateLibrariesFile") {
                outputDirectory.set(generatedResourcesDirectory)
            }

        val generateDescriptionFileTask = project.tasks
            .register<GenerateDescriptionFileTask>("generateDescriptionFile") {
                outputDirectory.set(generatedResourcesDirectory)
            }

        project.plugins.withType<JavaPlugin> {
            project.extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
                resources.srcDirs(
                    generateLibrariesFileTask,
                    generateDescriptionFileTask
                )
                project.configurations.getByName(compileOnlyConfigurationName).extendsFrom(library)
            }
        }

        project.tasks.withType<AbstractCopyTask> {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}