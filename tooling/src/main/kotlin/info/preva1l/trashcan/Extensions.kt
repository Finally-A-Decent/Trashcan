package info.preva1l.trashcan

import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven

/**
 * Created on 10/06/2025
 *
 * @author Preva1l
 */
fun DependencyHandler.paper(
    version: String,
    nms: Boolean = false,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    configurationAction: Action<ExternalModuleDependency> = nullAction()
) = extensions.getByType<TrashcanDependencyExtension>()
    .paper(version, nms, configuration, classifier, ext, configurationAction)

fun DependencyHandler.trashcan(
    version: String? = TrashcanDependencyExtension.BUNDLED_TRASHCAN_VERSION,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    configurationAction: Action<ExternalModuleDependency> = nullAction()
) = extensions.getByType<TrashcanDependencyExtension>()
    .trashcan(version, configuration, classifier, ext, configurationAction)

fun ExternalModuleDependency.setRemapped(remap: Boolean) {
    attributes {
        attribute(TrashcanExtension.REMAP_ATTRIBUTE, remap)
    }
}

fun RepositoryHandler.finallyADecent(
    name: String = "FinallyADecent",
    dev: Boolean = false,
    authenticated: Boolean = false
) {
    maven("https://repo.preva1l.info/${if (dev) "development" else "releases"}/") {
        this@maven.name = name
        if (authenticated) {
            credentials(PasswordCredentials::class)
        }
    }
}

@Suppress("unchecked_cast")
fun <T> nullAction(): Action<T> {
    return NullAction as Action<T>
}

private object NullAction : Action<Any> {
    override fun execute(t: Any) {}
}