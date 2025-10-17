package info.preva1l.trashcan

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.attributes.Attribute
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.property

abstract class TrashcanExtension(
    objects: ObjectFactory,
    private val repositories: RepositoryHandler
) {
    val remapAttribute: Attribute<Boolean> = REMAP_ATTRIBUTE

    val common: Property<Boolean> = objects.property<Boolean>().convention(true)

    val kotlin: Property<Boolean> = objects.property<Boolean>().convention(false)

    val paper: Property<Boolean> = objects.property<Boolean>().convention(false)

    val nms: Property<Boolean> = objects.property<Boolean>().convention(false)

    val librariesFileName: Property<String> = objects.property<String>().convention("libraries.json")

    @JvmOverloads
    fun finallyADecentRepository(
        name: String = "FinallyADecent",
        dev: Boolean = false,
        authenticated: Boolean = false
    ) {
        repositories.maven("https://repo.preva1l.info/${if (dev) "development" else "releases"}/") {
            this@maven.name = name
            if (authenticated) {
                credentials(PasswordCredentials::class)
            }
        }
    }

    companion object {
        val REMAP_ATTRIBUTE: Attribute<Boolean> = Attribute.of("remap", Boolean::class.javaObjectType)
    }
}