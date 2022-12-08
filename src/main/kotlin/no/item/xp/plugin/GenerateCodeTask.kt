package no.item.xp.plugin

import no.item.xp.plugin.parser.resolveMixinGraph
import no.item.xp.plugin.renderers.renderGlobalContentTypeMap
import no.item.xp.plugin.renderers.renderGlobalXDataMap
import no.item.xp.plugin.util.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

open class GenerateCodeTask @Inject constructor(objects: ObjectFactory, private val workerExecutor: WorkerExecutor) : DefaultTask() {
  @Input
  var singleQuote = false

  @Input
  var prependText = "// WARNING: This file was automatically generated by \"no.item.xp.codegen\". You may lose your changes if you edit it."

  @Incremental
  @InputFiles
  val inputFiles: ConfigurableFileCollection = objects.fileCollection()

  @OutputFiles
  val outputFiles: ConfigurableFileCollection = objects.fileCollection()

  @OutputDirectory
  val outputDir: RegularFileProperty = objects.fileProperty()

  @TaskAction
  private fun execute(inputChanges: InputChanges) {
    val workQueue = workerExecutor.noIsolation()

    val mixins = resolveMixinGraph(
      inputFiles.filter { file -> IS_MIXIN.matches(normalizeFilePath(file)) }
    )

    val rootOutputDir = outputDir.get().asFile

    inputChanges.getFileChanges(inputFiles).forEach { change ->
      val targetFile = getTargetFile(change.file, rootOutputDir)

      if (change.changeType == ChangeType.REMOVED && targetFile.delete()) {
        logger.lifecycle("Removed ${targetFile.absolutePath}")
      } else {
        workQueue.submit(GenerateTypeScriptWorkAction::class.java) {
          it.getXmlFile().set(change.file)
          it.getTargetFile().set(targetFile)
          it.getMixins().value(mixins)
          it.getSingleQuote().value(singleQuote)
          it.getPrependText().value(prependText)
          it.getAppName().value(project.property("appName") as String)
        }
      }
    }

    createContentTypeIndexFile(rootOutputDir)

    createXDataIndexFile(rootOutputDir)
  }

  private fun createContentTypeIndexFile(rootOutputDir: File) {
    val appName = project.property("appName") as String
    val files = inputFiles.files.filter { it.absolutePath.contains("content-types") }.sortedBy { it.name }
    val fileContent = renderGlobalContentTypeMap(files, appName)
    val targetFile = File(rootOutputDir.absolutePath + "/site/content-types/index.d.ts")
    targetFile.parentFile.mkdirs()
    targetFile.createNewFile()
    targetFile.writeText(prependText + "\n" + fileContent, Charsets.UTF_8)
    logger.lifecycle("Updated file: ${simpleFilePath(targetFile)}")
  }

  private fun createXDataIndexFile(rootOutputDir: File) {
    val appName = project.property("appName") as String
    val files = inputFiles.files.filter { it.absolutePath.contains("x-data") }.sortedBy { it.name }

    if (files.isNotEmpty()) {
      val fileContent = renderGlobalXDataMap(files, appName)
      val targetFile = File(rootOutputDir.absolutePath + "/site/x-data/index.d.ts")
      targetFile.parentFile.mkdirs()
      targetFile.createNewFile()
      targetFile.writeText(prependText + "\n" + fileContent, Charsets.UTF_8)
      logger.lifecycle("Updated file: ${simpleFilePath(targetFile)}")
    }
  }
}
