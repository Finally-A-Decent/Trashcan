package info.preva1l.trashcan

import org.gradle.api.provider.Property

/**
 * Created on 7/06/2025
 *
 * @author Preva1l
 */
interface PluginExtension {
    val name: Property<String>
    val description: Property<String>
    val mainClass: Property<String>
}