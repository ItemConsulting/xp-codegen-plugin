package no.item.xp.plugin.translatelist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.inject.Inject;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

abstract public class WriteUntranslatedPhrasesTask extends SourceTask {

  private Logger logger = Logging.getLogger("WriteUntranslatedPhrases");

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  @TaskAction
  public void WriteUntranslatedPhrases() {
    String propertyDirectory = System.getProperty("user.dir") + "\\src\\main\\resources\\i18n\\";
    String destinationFile = propertyDirectory + "phrases.tmp.properties";
    File destFile = new File(destinationFile);
    try {
      destFile.delete();
    }
    catch (Exception e) {
      logger.info("Could not delete file " + destinationFile, e);
    }

      // load the properties file that contains the translated phrases, do it only once for the task
    Properties prop = new Properties();
    try (InputStream input = Files.newInputStream(Path.of(propertyDirectory + "phrases.properties"))) {
      // load a properties file
      prop.load(input);
    }

    catch (IOException ex) {
      logger.error("Error loading translated phrases from \\src\\main\\resources\\i18n\\", ex);
    }

    WorkQueue workQueue = getWorkerExecutor().noIsolation();

    // run in parallel the xml parsing to search and log untranslated phrases
    getSource().filter(file -> file.getName().endsWith(".xml")).forEach(xmlFile -> {

      workQueue.submit(WriteUntranslatedPhrasesWorkAction.class, parameters -> {
        parameters.getSourceFile().set(xmlFile);
        parameters.getDestinationFileName().set(destinationFile);
        parameters.getTranslatedPhrases().set(prop.stringPropertyNames());
      });
    });
  }
}

