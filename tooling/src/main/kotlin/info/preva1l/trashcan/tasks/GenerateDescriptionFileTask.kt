package info.preva1l.trashcan.tasks

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.databind.util.StdConverter
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import info.preva1l.trashcan.TrashcanExtension
import info.preva1l.trashcan.description.paper.PaperPluginDescription
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType

/**
 * Created on 10/06/2025
 *
 * @author Preva1l
 */
abstract class GenerateDescriptionFileTask : DefaultTask() {
    init {
        group = "trashcan"
    }

    @get:OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun resolve() {
        val pluginDescription = if (project.extensions.getByType(TrashcanExtension::class).paper.get()) {
            project.extensions.getByType(PaperPluginDescription::class)
        } else {
            return
        }

        val factory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)

        val module = SimpleModule()
        @Suppress("UNCHECKED_CAST")
        module.addSerializer(
            StdDelegatingSerializer(
                NamedDomainObjectCollection::class.java,
                NamedDomainObjectCollectionConverter as Converter<NamedDomainObjectCollection<*>, *>
            )
        )

        val mapper = ObjectMapper(factory)
            .registerKotlinModule()
            .registerModule(module)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        mapper.writeValue(outputDirectory.file(pluginDescription.fileName).get().asFile, pluginDescription)
    }

    object NamedDomainObjectCollectionConverter : StdConverter<NamedDomainObjectCollection<Any>, Map<String, Any>>() {
        override fun convert(value: NamedDomainObjectCollection<Any>): Map<String, Any> {
            val namer = value.namer
            return value.associateBy { namer.determineName(it) }
        }
    }
}