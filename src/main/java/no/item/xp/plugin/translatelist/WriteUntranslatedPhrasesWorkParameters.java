package no.item.xp.plugin.translatelist;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

public interface WriteUntranslatedPhrasesWorkParameters extends WorkParameters {
  RegularFileProperty getSourceFile();

  Property<String> getDestinationFileName();

  ListProperty<String> getTranslatedPhrases();
}


