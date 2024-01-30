package no.item.xp.plugin.translatelist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

class UntranslatedListCreatorTest {

  final StringReader reader = new StringReader("<part xmlns=\"urn:enonic:xp:model:1.0\">\n" +
    "    <display-name i18n=\"articleHeader.displayName\">Article header</display-name>\n" +
    "    <description i18n=\"articleHeader.description\">Detailed view of an articles</description>\n" +
    "\n" +
    "    <form>\n" +
    "        <input name=\"showParentLink\" type=\"Checkbox\">\n" +
    "            <label i18n=\"articleHeader.showParentLink\">Show parent link</label>\n" +
    "            <default>checked</default>\n" +
    "        </input>\n" +
    "\n" +
    "        <input name=\"showPublished\" type=\"Checkbox\">\n" +
    "            <label i18n=\"articleHeader.showPublished\">Show published date</label>\n" +
    "            <default>checked</default>\n" +
    "        </input>\n" +
    "    </form>\n" +
    "</part>");

  @Test
  void getUntranslatedPhrases_withNothingTranslated() throws XMLStreamException {
    // Arrange
    final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(reader);
    List<String> translatedPhrases = List.of("Translated Phrase 1", "Translated Phrase 2");
    UntranslatedListCreator untranslatedListCreator = new UntranslatedListCreator(xmlReader, translatedPhrases);

    // Act
    Map<String, String> untranslatedPhrases = untranslatedListCreator.getUntranslatedPhrases();

    // Assert
    Assertions.assertEquals(4, untranslatedPhrases.size());
    Assertions.assertTrue(untranslatedPhrases.containsKey("articleHeader.displayName"));
    Assertions.assertTrue(untranslatedPhrases.containsKey("articleHeader.description"));
    Assertions.assertTrue(untranslatedPhrases.containsKey("articleHeader.showPublished"));
    Assertions.assertTrue(untranslatedPhrases.containsKey("articleHeader.showParentLink"));
    Assertions.assertEquals("Show published date", untranslatedPhrases.get("articleHeader.showPublished"));
  }

  @Test
  void getUntranslatedPhrases_withAllTranslated() throws XMLStreamException {
    // Arrange
    final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(reader);
    List<String> translatedPhrases = List.of("articleHeader.displayName", "articleHeader.description", "articleHeader.showPublished", "articleHeader.showParentLink");
    UntranslatedListCreator untranslatedListCreator = new UntranslatedListCreator(xmlReader, translatedPhrases);

    // Act
    Map<String, String> untranslatedPhrases = untranslatedListCreator.getUntranslatedPhrases();

    // Assert
    Assertions.assertEquals(0, untranslatedPhrases.size());
  }

  @Test
  void getUntranslatedPhrases_withPartialTranslated() throws XMLStreamException {
    // Arrange
    final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(reader);
    List<String> translatedPhrases = List.of("articleHeader.displayName", "articleHeader.showParentLink");
    UntranslatedListCreator untranslatedListCreator = new UntranslatedListCreator(xmlReader, translatedPhrases);

    // Act
    Map<String, String> untranslatedPhrases = untranslatedListCreator.getUntranslatedPhrases();

    // Assert
    Assertions.assertEquals(2, untranslatedPhrases.size());
    Assertions.assertTrue(untranslatedPhrases.containsKey("articleHeader.description"));
    Assertions.assertTrue(untranslatedPhrases.containsKey("articleHeader.showPublished"));
  }
}
