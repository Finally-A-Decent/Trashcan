package info.preva1l.trashcan

/**
 * Created on 11/06/2025
 *
 * @author Preva1l
 */
data class PaperProvidedDependency(
    val map: MutableMap<String, Any?> = mutableMapOf(
        "load" to LoadOrder.OMIT,
        "required" to true,
        "join_class_path" to true
    )
) {
    var name: String by map
    var load: LoadOrder by map
    var required: Boolean by map
    var join_class_path: Boolean by map

    enum class LoadOrder {
        BEFORE,
        AFTER,
        OMIT,
    }
}
