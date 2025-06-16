package info.preva1l.trashcan

import info.preva1l.trashcan.description.paper.PaperDependencyDefinition
import info.preva1l.trashcan.description.paper.PaperPluginDescription
import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.maven
import org.gradle.api.artifacts.Dependency as GDependency

abstract class TrashcanDependencyExtension(
    private val dependencies: DependencyHandler,
    private val repositories: RepositoryHandler,
    private val trashcanConfig: TrashcanExtension,
    private val extensions: ExtensionContainer
) {
    companion object {
        const val BUNDLED_TRASHCAN_VERSION: String = "1.2.0"
    }

    @JvmOverloads
    fun paper(
        version: String,
        nms: Boolean = false,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        configurationAction: Action<ExternalModuleDependency> = nullAction()
    ): ExternalModuleDependency {
        repositories.findByName("paper-maven-public") ?: run {
            repositories.maven("https://repo.papermc.io/repository/maven-public/") {
                name = "paper-maven-public"
            }
        }
        val name: String = if (nms) "dev-bundle" else "paper-api"
        val dep = dependencies.create("io.papermc.paper", name, version, configuration, classifier, ext)
        configurationAction(dep)
        dependencies.add("compileOnly", dep)
        return dep
    }

    @JvmOverloads
    fun trashcan(
        version: String? = BUNDLED_TRASHCAN_VERSION,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        configurationAction: Action<ExternalModuleDependency> = nullAction()
    ): List<ExternalModuleDependency> {
        val deps = mutableListOf<ExternalModuleDependency>()

        if (trashcanConfig.common.get()) {
            deps.add(trashcan("common", version, configuration, classifier, ext, configurationAction))
            if (trashcanConfig.kotlin.get()) {
                deps.add(trashcan("common-kotlin", version, configuration, classifier, ext, configurationAction))
            }
        }

        if (trashcanConfig.paper.get()) {
            deps.add(trashcan("paper", version, configuration, classifier, ext, configurationAction))
            if (trashcanConfig.kotlin.get()) {
                deps.add(trashcan("paper-kotlin", version, configuration, classifier, ext, configurationAction))
            }
        }

        return deps
    }

    @JvmOverloads
    fun trashcan(
        name: String,
        version: String? = BUNDLED_TRASHCAN_VERSION,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        configurationAction: Action<ExternalModuleDependency> = nullAction()
    ): ExternalModuleDependency {
        repositories.findByName("FinallyADecent") ?: run {
            repositories.finallyADecent()
        }

        val dep = dependencies.create("info.preva1l.trashcan", name, version, configuration, classifier, ext)
        configurationAction(dep)
        dependencies.add("implementation", dep)
        dependencies.add("annotationProcessor", dep)
        return dep
    }

    fun dependency(
        dependencyNotation: String,
        name: String,
        bootstrap: Boolean = false,
        action: Action<PaperDependencyDefinition> = nullAction()
    ): ExternalModuleDependency {
        val dep = dependencies.create(dependencyNotation) { isTransitive = false }
        dependencies.add("compileOnly", dep)

        extensions.getByType(PaperPluginDescription::class).apply {
            if (bootstrap) bootstrapDependencies.create(name, action)
            else serverDependencies.create(name, action)
        }

        return dep
    }

    fun dependency(
        dependencyNotation: Any,
        name: String,
        bootstrap: Boolean = false,
        action: Action<PaperDependencyDefinition> = nullAction()
    ): GDependency {
        val dep = dependencies.create(dependencyNotation)
        dependencies.add("compileOnly", dep)

        extensions.getByType(PaperPluginDescription::class).apply {
            if (bootstrap) bootstrapDependencies.create(name, action)
            else serverDependencies.create(name, action)
        }

        return dep
    }
}