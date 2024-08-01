package no.item.xp.plugin.phrases

import java.util.HashMap
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamReader

fun getAllI18nKeyValues(reader: XMLStreamReader): Map<String, String> {
  var i18nKey: String? = null

  val untranslatedPhrases = HashMap<String, String>()

  while (reader.hasNext()) {
    when (reader.next()) {
      XMLStreamConstants.START_ELEMENT -> {
        i18nKey = null
        for (i in 0..reader.attributeCount) {
          if (reader.getAttributeLocalName(i) == "i18n") {
            i18nKey = reader.getAttributeValue(i)
          }
        }
      }

      XMLStreamConstants.CHARACTERS ->
        if (i18nKey != null) {
          untranslatedPhrases[i18nKey] = reader.text
          i18nKey = null
        }

      XMLStreamConstants.END_ELEMENT ->
        if (i18nKey != null) {
          // no text found for the element, not sure if this should happen or not
          untranslatedPhrases[i18nKey] = ""
          i18nKey = null
        }
    }
  }

  return untranslatedPhrases
}
