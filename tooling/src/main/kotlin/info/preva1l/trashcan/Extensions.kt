package info.preva1l.trashcan

import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.getByType

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
    version: String? = "1.1.0",
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    configurationAction: Action<ExternalModuleDependency> = nullAction()
) = extensions.getByType<TrashcanDependencyExtension>()
    .trashcan(version, configuration, classifier, ext, configurationAction)

fun DependencyHandler.dependency(
    dependencyNotation: String,
    action: Action<PaperProvidedDependency>
) = extensions.getByType<TrashcanDependencyExtension>()
    .dependency(dependencyNotation, action)

fun DependencyHandler.dependency(
    dependencyNotation: Any,
    action: Action<PaperProvidedDependency>
) = extensions.getByType<TrashcanDependencyExtension>()
    .dependency(dependencyNotation, action)


fun ExternalModuleDependency.setRemapped(remap: Boolean) {
    attributes {
        attribute(TrashcanExtension.REMAP_ATTRIBUTE, remap)
    }
}

@Suppress("unchecked_cast")
fun <T> nullAction(): Action<T> {
    return NullAction as Action<T>
}

private object NullAction : Action<Any> {
    override fun execute(t: Any) {}
}