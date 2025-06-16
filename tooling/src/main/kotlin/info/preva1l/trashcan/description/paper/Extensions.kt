package info.preva1l.trashcan.description.paper

import info.preva1l.trashcan.TrashcanDependencyExtension
import info.preva1l.trashcan.nullAction
import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.getByType

fun DependencyHandler.dependency(
    dependencyNotation: String,
    name: String,
    bootstrap: Boolean = false,
    action: Action<PaperDependencyDefinition> = nullAction()
) = extensions.getByType<TrashcanDependencyExtension>()
    .dependency(dependencyNotation, name, bootstrap, action)

fun DependencyHandler.dependency(
    dependencyNotation: Any,
    name: String,
    bootstrap: Boolean = false,
    action: Action<PaperDependencyDefinition> = nullAction()
) = extensions.getByType<TrashcanDependencyExtension>()
    .dependency(dependencyNotation, name, bootstrap, action)