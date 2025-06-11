package info.preva1l.trashcan

import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.*
import org.gradle.api.artifacts.Dependency as GDependency

abstract class TrashcanDependencyExtension(
    private val dependencies: DependencyHandler,
    private val repositories: RepositoryHandler,
    private val trashcanConfig: TrashcanExtension
) {
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

        val dep = dependencies.create("io.papermc.paper", if (nms) "dev-bundle" else "paper-api", version, configuration, classifier, ext)
        configurationAction(dep)
        dependencies.add("compileOnly", dep)
        return dep
    }

    @JvmOverloads
    fun trashcan(
        version: String? = "1.1.0",
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
        version: String? = "1.1.0",
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        configurationAction: Action<ExternalModuleDependency> = nullAction()
    ) : ExternalModuleDependency {
        repositories.findByName("finally-a-decent") ?: run {
            repositories.maven("https://repo.preva1l.info/releases/") {
                this@maven.name = "finally-a-decent"
            }
        }

        val dep = dependencies.create("info.preva1l.trashcan", name, version, configuration, classifier, ext)
        configurationAction(dep)
        dependencies.add("implementation", dep)
        dependencies.add("annotationProcessor", dep)
        return dep
    }

    fun dependency(
        dependencyNotation: String,
        action: Action<PaperProvidedDependency>
    ) : ExternalModuleDependency {
        val dep = dependencies.create(dependencyNotation) { isTransitive = false }
        dependencies.add("compileOnly", dep)
        return dep
    }

    fun dependency(
        dependencyNotation: Any,
        action: Action<PaperProvidedDependency>
    ) : GDependency {
        val dep = dependencies.create(dependencyNotation)
        dependencies.add("compileOnly", dep)
        return dep
    }
}