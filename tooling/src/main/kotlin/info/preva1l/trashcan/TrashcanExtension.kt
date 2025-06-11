package info.preva1l.trashcan

import org.gradle.api.attributes.Attribute
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

abstract class TrashcanExtension(
    objects: ObjectFactory,
) {
    val remapAttribute: Attribute<Boolean> = REMAP_ATTRIBUTE

    val common: Property<Boolean> = objects.property<Boolean>().convention(true)

    val kotlin: Property<Boolean> = objects.property<Boolean>().convention(false)

    val paper: Property<Boolean> = objects.property<Boolean>().convention(false)

    val librariesFileName: Property<String> = objects.property<String>().convention("libraries.json")

    companion object {
        val REMAP_ATTRIBUTE: Attribute<Boolean> = Attribute.of("remap", Boolean::class.javaObjectType)
    }
}