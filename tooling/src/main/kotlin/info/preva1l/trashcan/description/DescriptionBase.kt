package info.preva1l.trashcan.description

import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.api.tasks.Internal

/**
 * Created on 13/06/2025
 *
 * @author Preva1l
 */
abstract class DescriptionBase {
    @get:Internal @get:JsonIgnore internal abstract val fileName: String
}