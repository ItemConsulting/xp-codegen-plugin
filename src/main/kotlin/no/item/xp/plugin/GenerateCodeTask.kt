package no.item.xp.plugin

import arrow.core.flatMap
import arrow.core.right
import no.item.xp.plugin.extensions.getFormNode
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.parser.parseObjectTypeModel
import no.item.xp.plugin.parser.resolveMixinGraph
import no.item.xp.plugin.renderers.renderGlobalComponentMap
import no.item.xp.plugin.renderers.renderGlobalContentTypeMap
import no.item.xp.plugin.renderers.renderGlobalXDataMap
import no.item.xp.plugin.renderers.ts.renderTypeModelAsTypeScript
import no.item.xp.plugin.util.*
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Dependency
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
import java.net.URI
import java.nio.file.Paths
import java.util.zip.ZipFile
import javax.inject.Inject
import kotlin.io.path.createDirectories
import kotlin.io.path.nameWithoutExtension

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

    createJarMagic(rootOutputDir)

    createContentTypeIndexFile(rootOutputDir)

    createComponentIndexFile(rootOutputDir, "parts", "XpPartMap")
    createComponentIndexFile(rootOutputDir, "layouts", "XpLayoutMap")
    createComponentIndexFile(rootOutputDir, "pages", "XpPageMap")

    createXDataIndexFile(rootOutputDir)
  }

  private fun createJarMagic(rootOutputDir: File) {
    val dependencies = getDependencies()
    val jarFiles = getDependencyJarFiles(dependencies)

    jarFiles.forEach {
      val zip = ZipFile(it)

      val enonicZipEntries = zip.entries().asSequence()
        .filter { it.name.startsWith("site") }
        .filter { it.name.endsWith(".xml") }

      enonicZipEntries.forEach { zipEntry ->
        val stream = zip.getInputStream(zipEntry)
        parseXml(stream)
          .flatMap { it.getFormNode() }
          .fold(
            {
              ObjectTypeModel(Paths.get(zipEntry.name).fileName.nameWithoutExtension, emptyList()).right()
            },
            {
              parseObjectTypeModel(it, Paths.get(zipEntry.name).fileName.nameWithoutExtension, emptyList())
            }
          )
          .fold(
            {
              logger.error("ERROR in: ${zipEntry.name}")
              logger.error(it.message)
            },
            {
              val fileContent = renderTypeModelAsTypeScript(it)
              println(fileContent)

              var targetFilePath = Paths.get(rootOutputDir.absolutePath, "tmp", zipEntry.name)
              targetFilePath = Paths.get(targetFilePath.parent.toString(), "index.d.ts")
              val targetFile = File(targetFilePath.toUri())
              targetFile.parentFile.mkdirs()
              targetFile.createNewFile()
              targetFile.writeText(fileContent, Charsets.UTF_8)
            })
      }
    }
  }

  private fun getDependencies(): List<Dependency> {
    return project.configurations
      .flatMap { it.allDependencies }
      .distinct()
      .sortedBy { it.name }
  }

  private fun getDependencyJarFiles(dependencies: List<Dependency>): List<File> {
    return dependencies
      .map {
        Paths.get(
          project.gradle.gradleUserHomeDir.path,
          "caches",
          "modules-2",
          "files-2.1",
          it.group,
          it.name,
          it.version
        )
      }
      .flatMap { File(it.toUri()).listFiles()?.asList() ?: listOf() }
      .filterNotNull()
      .flatMap { it.listFiles()?.asList() ?: listOf() }
      .filterNotNull()
      .filter { it.extension == "jar" }
      .toList()
  }

  private fun createContentTypeIndexFile(rootOutputDir: File) {
    val appName = project.property("appName") as String
    val files = inputFiles.files.filter { it.absolutePath.contains(concatFileName("resources", "site", "content-types")) }.sortedBy { it.name }

    if (files.isNotEmpty()) {
      val fileContent = renderGlobalContentTypeMap(files, appName)
      val targetFile = File(rootOutputDir.absolutePath + "/site/content-types/index.d.ts")
      targetFile.parentFile.mkdirs()
      targetFile.createNewFile()
      targetFile.writeText(prependText + "\n" + fileContent, Charsets.UTF_8)
      logger.lifecycle("Updated file: ${simpleFilePath(targetFile)}")
    }
  }

  private fun createComponentIndexFile(rootOutputDir: File, componentTypeName: String, interfaceName: String) {
    val appName = project.property("appName") as String
    val files = inputFiles.files.filter { it.absolutePath.contains(concatFileName("resources", "site", componentTypeName)) }.sortedBy { it.name }

    if (files.isNotEmpty()) {
      val fileContent = renderGlobalComponentMap(files, appName, interfaceName)
      val targetFile = File( concatFileName(rootOutputDir.absolutePath, "site", componentTypeName, "index.d.ts"))
      targetFile.parentFile.mkdirs()
      targetFile.createNewFile()
      targetFile.writeText(prependText + "\n" + fileContent, Charsets.UTF_8)
      logger.lifecycle("Updated file: ${simpleFilePath(targetFile)}")
      }
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
