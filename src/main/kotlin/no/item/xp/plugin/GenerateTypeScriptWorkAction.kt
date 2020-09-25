package no.item.xp.plugin

import arrow.core.extensions.either.monad.flatMap
import no.item.xp.plugin.extensions.getFormNode
import no.item.xp.plugin.models.InterfaceModel
import no.item.xp.plugin.parser.parseInterfaceModel
import no.item.xp.plugin.renderers.ts.renderInterfaceModel
import no.item.xp.plugin.util.parseXml
import no.item.xp.plugin.util.simpleFilePath
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

interface CodegenWorkParameters : WorkParameters {
  fun getXmlFile(): RegularFileProperty
  fun getTargetFile(): RegularFileProperty
  fun getMixins(): ListProperty<InterfaceModel>
}

abstract class GenerateTypeScriptWorkAction : WorkAction<CodegenWorkParameters> {
  private val logger = Logging.getLogger("GenerateTypeScript")

  override fun execute() {
    try {
      val file = parameters.getXmlFile().get().asFile
      val targetFile = parameters.getTargetFile().get().asFile
      val mixins = parameters.getMixins().get()

      parseXml(file)
        .flatMap { doc -> doc.getFormNode() }
        .flatMap { formNode -> parseInterfaceModel(formNode, targetFile.nameWithoutExtension, mixins) }
        .fold(
          {
            if (it is NoFormException) {
              logger.debug("No <form> found in: ${simpleFilePath(file)}")
            } else {
              logger.error("ERROR in: ${simpleFilePath(file)}")
              logger.error(it.message)
            }
          },
          { model ->
            if (model.fields.isNotEmpty()) {
              targetFile.writeText(renderInterfaceModel(model), Charsets.UTF_8)
              logger.lifecycle("Updated file: ${simpleFilePath(targetFile)}")
            }
          }
        )
    } catch (e: Exception) {
      logger.error("Can't parse file", e)
    }
  }
}
