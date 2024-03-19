package no.item.xp.plugin

import arrow.core.flatMap
import arrow.core.right
import no.item.xp.plugin.extensions.getFormNode
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.parser.parseObjectTypeModel
import no.item.xp.plugin.renderers.renderSiteConfig
import no.item.xp.plugin.renderers.ts.renderTypeModelAsTypeScript
import no.item.xp.plugin.util.concatFileName
import no.item.xp.plugin.util.parseXml
import no.item.xp.plugin.util.simpleFilePath
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

interface CodegenWorkParameters : WorkParameters {
  fun getXmlFile(): RegularFileProperty
  fun getTargetFile(): RegularFileProperty
  fun getMixins(): ListProperty<ObjectTypeModel>
  fun getPrependText(): Property<String>
  fun getSingleQuote(): Property<Boolean>
  fun getAppName(): Property<String>
}

abstract class GenerateTypeScriptWorkAction : WorkAction<CodegenWorkParameters> {
  private val logger = Logging.getLogger("GenerateTypeScript")

  override fun execute() {
    try {
      val file = parameters.getXmlFile().get().asFile
      val targetFile = parameters.getTargetFile().get().asFile
      val mixins = parameters.getMixins().get()

      parseXml(file.inputStream())
        .flatMap { doc -> doc.getFormNode() }
        .fold(
          {
            ObjectTypeModel(file.nameWithoutExtension, emptyList()).right()
          },
          {
            parseObjectTypeModel(it, file.nameWithoutExtension, mixins)
          }
        )
        .fold(
          {
            logger.error("ERROR in: ${simpleFilePath(file)}")
            logger.error(it.message)
          },
          {
            var fileContent =
              if (file.absolutePath.endsWith(concatFileName("resources", "site", "site.xml"))) {
                renderSiteConfig(it)
              } else {
                renderTypeModelAsTypeScript(it)
              }

            if (parameters.getSingleQuote().get()) {
              fileContent = fileContent.replace("\"", "'")
            }

            val prependText = parameters.getPrependText().get()
            if (prependText.isNotEmpty()) {
              fileContent = prependText + "\n" + fileContent
            }

            targetFile.parentFile.mkdirs()
            targetFile.createNewFile()
            targetFile.writeText(fileContent, Charsets.UTF_8)
            logger.lifecycle("Updated file: ${simpleFilePath(targetFile)}")
          }
        )
    } catch (e: Exception) {
      logger.error("Can't parse file", e)
    }
  }
}
