package info.preva1l.trashcan.description.paper

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

data class Permission(@Input @JsonIgnore val name: String) {
    @Input @Optional var description: String? = null
    @Input @Optional var default: Default? = null
    var children: List<String>?
        @Internal @JsonIgnore get() = childrenMap?.filterValues { it }?.keys?.toList()
        set(value) {
            childrenMap = value?.associateWith { true }
        }
    @Input @Optional @JsonProperty("children") var childrenMap: Map<String, Boolean>? = null

    enum class Default {
        @JsonProperty("true") TRUE,
        @JsonProperty("false") FALSE,
        @JsonProperty("op") OP,
        @JsonProperty("!op") NOT_OP
    }
}
