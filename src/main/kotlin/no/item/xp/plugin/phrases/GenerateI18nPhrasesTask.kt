package no.item.xp.plugin.phrases

import no.item.xp.plugin.util.concatFileName
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.inject.Inject
import javax.xml.stream.XMLInputFactory

open class GenerateI18nPhrasesTask
  @Inject
  constructor() : SourceTask() {
    @TaskAction
    fun execute() {
      val propertyDirectory = concatFileName(System.getProperty("user.dir"), "src", "main", "resources", "i18n")
      val destinationFilePath = concatFileName(propertyDirectory, "phrases.tmp.properties")
      val destFile = File(destinationFilePath)

      try {
        if (destFile.exists()) {
          destFile.delete()
        }
      } catch (e: Exception) {
        logger.info("Could not delete file $destinationFilePath", e)
      }

      // load the properties file that contains the translated phrases, do it only once for the task
      val existingProps = Properties()
      try {
        Files.newInputStream(Path.of(concatFileName(propertyDirectory, "phrases.properties"))).use { input ->
          // load a properties file
          existingProps.load(input)
        }
      } catch (ex: IOException) {
        logger.error("Error loading translated phrases from $propertyDirectory", ex)
      }

      val phrases =
        source
          .filter { file -> file.name.endsWith(".xml") }
          .map { file -> getPhrasesFromXMlFile(file).filterKeys { it !in existingProps.keys } }
          .fold(HashMap<String, String>()) { res, b ->
            res.putAll(b)
            res
          }

      exportUntranslatedPhrases(phrases, destinationFilePath)
    }

    private fun getPhrasesFromXMlFile(sourceFile: File): Map<String, String> {
      try {
        val factory = XMLInputFactory.newInstance()

        val reader = factory.createXMLStreamReader(FileReader(sourceFile.absolutePath))
        val allPhrases = getAllI18nKeyValues(reader)
        reader.close()

        return allPhrases
      } catch (e: Exception) {
        logger.error("Error checking for untranslated text in the xml files", e)
        return HashMap()
      }
    }

    private fun exportUntranslatedPhrases(
      untranslatedPhrases: Map<String, String>,
      destinationFilePath: String,
    ) {
      val prop = SortedStoreProperties()
      // load the properties that other work action may have written in parallel, this is why the method is synchronized
      try {
        Files.newInputStream(Path.of(destinationFilePath)).use { input ->
          // load a properties file
          prop.load(input)
        }
      } catch (ex: IOException) {
        // could happen as the file does not exist yet
        logger.info("could not load phrases.tmp.properties", ex)
      }

      if (untranslatedPhrases.isNotEmpty()) {
        prop.putAll(untranslatedPhrases)

        // write the properties to the same file with the untranslated phrases appended
        try {
          Files.newOutputStream(Path.of(destinationFilePath)).use { output ->
            // load a properties file
            prop.store(output, null)
          }
        } catch (ex: IOException) {
          logger.error("Error writing phrases.tmp.properties with the untranslated text", ex)
        }
      }

      logger.lifecycle("Wrote ${prop.size} entries to ${Path.of(destinationFilePath).toUri()}")
    }
  }
