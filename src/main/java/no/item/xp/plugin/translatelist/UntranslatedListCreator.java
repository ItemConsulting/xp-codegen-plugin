package no.item.xp.plugin.translatelist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.jetbrains.annotations.NotNull;

public class UntranslatedListCreator {
  private Logger logger = Logging.getLogger("WriteUntranslatedPhrases");

  private final XMLStreamReader reader;
  private final List<String> translatedPhrases;

  public UntranslatedListCreator(@NotNull XMLStreamReader reader, List<String> translatedPhrases) {
    this.reader = reader;
    this.translatedPhrases = translatedPhrases;
  }

  public @NotNull Map<String, String> getUntranslatedPhrases() throws XMLStreamException {
    String xmlElementToBeTranslated = null;

    Map<String, String> untranslatedPhrases = new HashMap<>();
    while (reader.hasNext()) {
      int eventType = reader.next();

      switch (eventType) {
        case XMLStreamConstants.START_ELEMENT:
          xmlElementToBeTranslated = null;
          for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeLocalName(i).equals("i18n")) {
              xmlElementToBeTranslated = reader.getAttributeValue(i);
            }
          }

          break;
        case XMLStreamConstants.CHARACTERS:
          if (xmlElementToBeTranslated != null) {
            addToUntranslatedPhrases(untranslatedPhrases, xmlElementToBeTranslated, reader.getText());
            xmlElementToBeTranslated = null;
          }
          break;
        case XMLStreamConstants.END_ELEMENT:
          if (xmlElementToBeTranslated != null) {
            // no text found for the element, not sure if this should happen or not
            addToUntranslatedPhrases(untranslatedPhrases, xmlElementToBeTranslated, "");
            xmlElementToBeTranslated = null;

          }
          break;
      }
    }
    return untranslatedPhrases;
  }

  private void addToUntranslatedPhrases(Map<String, String> untranslatedPhrases, String key, String value) {
    final String lookfor =  key;
    if (translatedPhrases.stream().noneMatch(phase -> phase.equals(lookfor))) {
      logger.info("Adding to the map ${key} = ${value}");
      untranslatedPhrases.put(key, value);
    }
  }
}

