package info.preva1l.trashcan.description.paper

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import info.preva1l.trashcan.description.DescriptionBase
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

@JsonNaming(KebabCaseStrategy::class)
class PaperPluginDescription(
    project: Project
) : DescriptionBase() {
    @JsonIgnore override val fileName: String = "paper-plugin.yml"

    @Input var name: String = project.rootProject.name
    @Input var version: String = project.version.toString()
    @Input lateinit var main: String
    @Input lateinit var apiVersion: String
    @Input var loader: String = "info.preva1l.trashcan.extension.libloader.BaseLibraryLoader"
    @Input @Optional var bootstrapper: String? = null
    @Input @Optional var description: String? = null
    @Input @Optional var load: PluginLoadOrder? = null
    @Input @Optional var author: String? = null
    @Input @Optional var authors: List<String>? = null
    @Input @Optional var contributors: List<String>? = listOf("Preva1l")
    @Input @Optional var website: String? = null
    @Input @Optional var prefix: String? = null
    @Input @Optional @JsonProperty("defaultPerm") var defaultPermission: Permission.Default? = null
    @Input @Optional var provides: List<String>? = null
    @Input @Optional var hasOpenClassloader: Boolean? = null
    @Input @Optional var foliaSupported: Boolean? = null

    @Nested @Optional @JsonIgnore
    var serverDependencies: NamedDomainObjectContainer<PaperDependencyDefinition> = project.container(PaperDependencyDefinition::class.java)
    @Nested @Optional @JsonIgnore
    var bootstrapDependencies: NamedDomainObjectContainer<PaperDependencyDefinition> = project.container(PaperDependencyDefinition::class.java)

    @JsonGetter
    fun dependencies(): Map<String, NamedDomainObjectContainer<PaperDependencyDefinition>> = mapOf(
        "server" to serverDependencies,
        "bootstrap" to bootstrapDependencies,
    )

    @Nested val permissions: NamedDomainObjectContainer<Permission> = project.container(Permission::class.java)
}