package no.item.xp.plugin.translatelist;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.workers.WorkAction;

public abstract class WriteUntranslatedPhrasesWorkAction implements WorkAction<WriteUntranslatedPhrasesWorkParameters> {
  private static Logger logger = Logging.getLogger("WriteUntranslatedPhrases");
  @Override
  public void execute() {

      File sourceFile = getParameters().getSourceFile().getAsFile().get();
      List<String> translatedPhrases = getParameters().getTranslatedPhrases().get();
      String destinationFileName = getParameters().getDestinationFileName().get();

      logger.lifecycle(String.format("Parsing XML file %s for missing translations...", sourceFile.getName()));
      try {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(sourceFile.getAbsolutePath()));

        UntranslatedListCreator untranslatedListCreator = new UntranslatedListCreator(reader, translatedPhrases);
        Map<String, String> untranslatedPhrases = untranslatedListCreator.getUntranslatedPhrases();
        reader.close();
        exportUntranslatedPhrases(untranslatedPhrases, destinationFileName);

      } catch (Exception e) {
        logger.error("Error checking for untranslated text in the xml files", e);
      }
  }


  public static synchronized void exportUntranslatedPhrases(Map<String, String> untranslatedPhrases, String destinationFileName) {

    Properties prop = new Properties();
    // load the properties that other work action may have written in parallel, this is why the method is synchronized
    try (InputStream input = Files.newInputStream(Path.of(destinationFileName))) {
      // load a properties file
      prop.load(input);
    }

    catch (IOException ex) {
      // could happen as the file does not exist yet
      logger.info("could not load phrases.tmp.properties", ex);
    }

    prop.putAll(untranslatedPhrases);

    // write the properties to the same file with the untranslated phrases appended
    try (OutputStream output = Files.newOutputStream(Path.of(destinationFileName))) {
      // load a properties file
      prop.store(output, null);
    }

    catch (IOException ex) {
      logger.error("Error writing phrases.tmp.properties with the untranslated text", ex);
    }

  }
}
