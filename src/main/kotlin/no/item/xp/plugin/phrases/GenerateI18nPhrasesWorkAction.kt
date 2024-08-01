package no.item.xp.plugin.phrases

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.stream.XMLInputFactory

interface WriteUntranslatedPhrasesWorkParameters : WorkParameters {
  fun getSourceFile(): RegularFileProperty

  fun getDestinationFileName(): Property<String>

  fun getTranslatedPhrases(): ListProperty<String>
}

abstract class GenerateI18nPhrasesWorkAction : WorkAction<WriteUntranslatedPhrasesWorkParameters> {
  override fun execute() {
    val sourceFile = parameters.getSourceFile().asFile.get()
    val translatedPhrases: List<String> = parameters.getTranslatedPhrases().get()
    val destinationFileName = parameters.getDestinationFileName().get()

    try {
      val factory = XMLInputFactory.newInstance()

      val reader = factory.createXMLStreamReader(FileReader(sourceFile.absolutePath))
      val allPhrases = getAllI18nKeyValues(reader)
      reader.close()

      val untranslatedPhrases = allPhrases.filterKeys { it!in translatedPhrases }
      exportUntranslatedPhrases(untranslatedPhrases, destinationFileName)
    } catch (e: Exception) {
      logger.error("Error checking for untranslated text in the xml files", e)
    }
  }

  companion object {
    private val logger: Logger = Logging.getLogger("WriteUntranslatedPhrases")

    @Synchronized
    fun exportUntranslatedPhrases(
      untranslatedPhrases: Map<String, String>,
      destinationFileName: String,
    ) {
      val prop = SortedStoreProperties()
      // load the properties that other work action may have written in parallel, this is why the method is synchronized
      try {
        Files.newInputStream(Path.of(destinationFileName)).use { input ->
          // load a properties file
          prop.load(input)
        }
      } catch (ex: IOException) {
        // could happen as the file does not exist yet
        logger.info("could not load phrases.tmp.properties", ex)
      }

      prop.putAll(untranslatedPhrases)

      // write the properties to the same file with the untranslated phrases appended
      try {
        Files.newOutputStream(Path.of(destinationFileName)).use { output ->
          // load a properties file
          prop.store(output, null)
        }
      } catch (ex: IOException) {
        logger.error("Error writing phrases.tmp.properties with the untranslated text", ex)
      }
    }
  }
}
