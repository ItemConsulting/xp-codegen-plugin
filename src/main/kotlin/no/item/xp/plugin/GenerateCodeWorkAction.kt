package no.item.xp.plugin

import arrow.core.extensions.either.monad.flatMap
import no.item.xp.plugin.extensions.getFormNode
import no.item.xp.plugin.extensions.getNodeAttribute
import no.item.xp.plugin.models.InterfaceModel
import no.item.xp.plugin.parser.parseInterfaceModel
import no.item.xp.plugin.renderers.iots.renderInterfaceModelAsIoTs
import no.item.xp.plugin.renderers.jsdoc.renderInterfaceModelAsJSDoc
import no.item.xp.plugin.renderers.ts.renderInterfaceModelAsTypeScript
import no.item.xp.plugin.util.FileType
import no.item.xp.plugin.util.parseXml
import no.item.xp.plugin.util.simpleFilePath
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.File

interface CodegenWorkParameters : WorkParameters {
  fun getXmlFile(): RegularFileProperty
  fun getTargetFile(): RegularFileProperty
  fun getMixins(): ListProperty<InterfaceModel>
  fun getFileType(): Property<FileType>
}

abstract class GenerateTypeScriptWorkAction : WorkAction<CodegenWorkParameters> {
  private val logger = Logging.getLogger("GenerateTypeScript")

  override fun execute() {
    try {
      val file = parameters.getXmlFile().get().asFile
      val targetFile = parameters.getTargetFile().get().asFile
      val mixins = parameters.getMixins().get()
      val defaultFileType = parameters.getFileType().get()

      parseXml(file)
        .flatMap { doc -> doc.getFormNode() }
        .flatMap { formNode ->
          parseInterfaceModel(formNode, targetFile.nameWithoutExtension, mixins)
            .map { model ->
              val fileType = parseFileTypeStr(formNode.getNodeAttribute("codegen-output"), defaultFileType, file)
              Pair(model, fileType)
            }
        }
        .fold(
          {
            if (it is NoFormException) {
              logger.debug("No <form> found in: ${simpleFilePath(file)}")
            } else {
              logger.error("ERROR in: ${simpleFilePath(file)}")
              logger.error(it.message)
            }
          },
          { (model: InterfaceModel, fileType: FileType) ->
            if (model.fields.isNotEmpty()) {
              val fileContent = when (fileType) {
                FileType.JSDoc -> renderInterfaceModelAsJSDoc(model)
                FileType.IoTs -> renderInterfaceModelAsIoTs(model)
                FileType.TypeScript -> renderInterfaceModelAsTypeScript(model)
                FileType.None -> null
              }

              if (fileContent != null) {
                val writeFile =
                  if (defaultFileType != fileType) {
                    File(targetFile.parent + "/" + targetFile.nameWithoutExtension + fileType.filePostfix)
                  }
                  else targetFile

                writeFile.writeText(fileContent, Charsets.UTF_8)
                logger.lifecycle("Updated file: ${simpleFilePath(targetFile)}")
              } else if (targetFile.delete()) {
                logger.lifecycle("Removed ${targetFile.absolutePath}")
              }
            }
          }
        )
    } catch (e: Exception) {
      logger.error("Can't parse file", e)
    }
  }

  private fun parseFileTypeStr(str: String?, defaultValue: FileType, file: File): FileType {
    return try {
      return str?.let { FileType.valueOf(it) } ?: defaultValue
    } catch (e: IllegalArgumentException) {
      logger.warn("The `codegen-output` value (\"${str}\") on <form> was not recognized in: ${simpleFilePath(file)}", e)
      defaultValue
    }
  }
}
