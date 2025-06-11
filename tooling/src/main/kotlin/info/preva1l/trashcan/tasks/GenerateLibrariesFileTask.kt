package info.preva1l.trashcan.tasks

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import info.preva1l.trashcan.Dependency
import info.preva1l.trashcan.Libraries
import info.preva1l.trashcan.Repository
import info.preva1l.trashcan.TrashcanExtension
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.FileWriter
import java.net.URI

/**
 * Created on 10/06/2025
 *
 * @author Preva1l
 */
abstract class GenerateLibrariesFileTask : DefaultTask() {
    init {
        group = "trashcan"
    }

    @get:OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty()

    private val libraries = Libraries(mutableMapOf())

    private val configuration: Configuration = project.configurations.getByName("library")

    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    private val repositories = (project.rootProject.repositories + project.repositories)
        .filterIsInstance<MavenArtifactRepository>()

    @TaskAction
    fun resolve() {
        if (repositories.isEmpty()) {
            logger.warn("No Maven repositories found to search")
            return
        }

        val firstLevelDeps = configuration.resolvedConfiguration.firstLevelModuleDependencies
            .associateBy { "${it.moduleGroup}:${it.moduleName}" }
        
        val remapValues = configuration.dependencies
            .filterIsInstance<ExternalModuleDependency>()
            .associateBy { "${it.module.group}:${it.module.name}" }
            .mapValues { it.value.attributes.getAttribute(TrashcanExtension.REMAP_ATTRIBUTE) == true }

        logger.info("Resolving repository URLs for ${configuration.incoming.artifacts.artifacts.size} artifacts...")

        configuration.incoming.artifacts.artifacts.forEach { artifact ->
            resolveArtifactRepository(artifact, repositories, firstLevelDeps, remapValues)
        }

        val librariesFile = outputDirectory.file(
            project.extensions.getByType<TrashcanExtension>().librariesFileName
        ).get().asFile

        FileWriter(librariesFile).use {
            gson.toJson(
                libraries,
                Libraries::class.java,
                it
            )
        }
    }

    private fun resolveArtifactRepository(
        artifact: ResolvedArtifactResult,
        repositories: List<MavenArtifactRepository>,
        firstLevelDeps: Map<String, ResolvedDependency>,
        remapValues: Map<String, Boolean>
    ) {
        var dependency = parseDependencyGav(artifact.id.componentIdentifier.displayName) ?: return
        val depKey = "${dependency.group}:${dependency.artifact}"

        if (!firstLevelDeps.containsKey(depKey)) return

        dependency = dependency.remapped(remapValues[depKey] == true)

        val jarFileName = "${dependency.artifact}-${dependency.fullVersion}.jar"
        val artifactPath = "${dependency.group.replace(".", "/")}/${dependency.artifact}/${dependency.version}/$jarFileName"

        repositories.forEach { repo ->
            val baseUrl = "${repo.url.toString().trimEnd('/')}/"
            val candidateUrl = "$baseUrl$artifactPath"

            if (checkUrlExists(candidateUrl)) {
                println("Resolved: ${dependency.asGavCoordinate()} -> $candidateUrl")
                libraries.repositories.getOrPut(repo.name) { Repository.named(repo.name, baseUrl) }.dependencies.add(dependency)
                return
            }
        }

        logger.warn("Could not resolve repository for: ${dependency.asGavCoordinate()}")
    }

    private fun parseDependencyGav(gav: String): Dependency? {
        val parts = gav.split(":")

        return when {
            parts.size >= 3 -> Dependency(
                parts[0],
                parts[1],
                parts[2],
                if (parts.size >= 4) parts[3] else null,
                false
            )
            else -> {
                logger.warn("Invalid dependency format: $gav")
                null
            }
        }
    }

    private fun checkUrlExists(url: String): Boolean {
        return try {
            URI.create(url).toURL().openStream().use { stream ->
                stream != null
            }
        } catch (e: Exception) {
            false
        }
    }
}