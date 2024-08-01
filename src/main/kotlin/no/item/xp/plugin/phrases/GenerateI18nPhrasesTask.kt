package no.item.xp.plugin.phrases

import no.item.xp.plugin.util.concatFileName
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.inject.Inject

open class GenerateI18nPhrasesTask
  @Inject
  constructor(private val workerExecutor: WorkerExecutor) : SourceTask() {
    @TaskAction
    fun execute() {
      val propertyDirectory = concatFileName(System.getProperty("user.dir"), "src", "main", "resources", "i18n")
      val destinationFile = concatFileName(propertyDirectory, "phrases.tmp.properties")
      val destFile = File(destinationFile)

      try {
        if (destFile.exists()) {
          destFile.delete()
        }
      } catch (e: Exception) {
        logger.info("Could not delete file $destinationFile", e)
      }

      // load the properties file that contains the translated phrases, do it only once for the task
      val prop = Properties()
      try {
        Files.newInputStream(Path.of(concatFileName(propertyDirectory, "phrases.properties"))).use { input ->
          // load a properties file
          prop.load(input)
        }
      } catch (ex: IOException) {
        logger.error("Error loading translated phrases from $propertyDirectory", ex)
      }

      val workQueue = workerExecutor.noIsolation()

      source
        .filter { file -> file.name.endsWith(".xml") }
        .forEach { xmlFile ->
          workQueue.submit(GenerateI18nPhrasesWorkAction::class.java) {
            it.getSourceFile().set(xmlFile)
            it.getDestinationFileName().set(destinationFile)
            it.getTranslatedPhrases().set(prop.stringPropertyNames())
          }
        }
    }
  }
