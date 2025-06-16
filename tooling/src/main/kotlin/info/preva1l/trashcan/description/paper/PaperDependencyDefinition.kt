package info.preva1l.trashcan.description.paper

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.gradle.api.tasks.Input

@JsonNaming(KebabCaseStrategy::class)
data class PaperDependencyDefinition(@Input @JsonIgnore val name: String) {
    @Input var load: RelativeLoadOrder = RelativeLoadOrder.OMIT
    @Input var required: Boolean = true
    @Input var joinClasspath: Boolean = true

    enum class RelativeLoadOrder {
        BEFORE,
        AFTER,
        OMIT,
    }
}