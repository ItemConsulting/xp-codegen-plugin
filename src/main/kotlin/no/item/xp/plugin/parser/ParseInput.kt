package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.extensions.getNodeAttribute
import no.item.xp.plugin.models.*
import org.w3c.dom.Node

const val REGEX_DATE = "^\\d{4}-([0]\\d|1[0-2])-([0-2]\\d|3[01])\$"
const val REGEX_TIME = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$"
const val REGEX_DATETIME = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?\$"

fun parseInput(inputNode: Node): InterfaceModelField? {
  val unknownField = parseUnknownField(inputNode)
  val type = inputNode.getNodeAttribute("type")

  if (unknownField != null && type != null) {
    return when (type.lowercase()) {
      "geopoint",
      "htmlarea",
      "contentselector",
      "imageselector",
      "contenttypefilter",
      "mediaselector",
      "attachmentuploader",
      "customselector",
      "tag" ->
        StringField(unknownField)
      "textarea",
      "textline" -> {
        val regexp = inputNode.getChildNodeAtXPath("config/regexp")?.textContent
        val maxLength = inputNode.getChildNodeAtXPath("config/max-length")?.textContent

        if (regexp != null || maxLength != null) {
          StringFieldWithValidation(unknownField, regexp, maxLength?.toIntOrNull())
        } else {
          StringField(unknownField)
        }
      }
      "date" ->
        StringFieldWithValidation(unknownField, REGEX_DATE)
      "time" ->
        StringFieldWithValidation(unknownField, REGEX_TIME)
      "datetime" ->
        StringFieldWithValidation(unknownField, REGEX_DATETIME)
      "checkbox" ->
        BooleanField(unknownField.copy(isNullable = false, isArray = false))
      "combobox" ->
        UnionOfStringLiteralField(unknownField, parseConfigOptionValue(inputNode))
      "long",
      "double" -> {
        val min = inputNode.getChildNodeAtXPath("config/min")?.textContent
        val max = inputNode.getChildNodeAtXPath("config/max")?.textContent

        if (min !== null || max !== null) {
          NumberFieldWithValidation(unknownField, min?.toIntOrNull(), max?.toIntOrNull())
        } else {
          NumberField(unknownField)
        }
      }
      "radiobutton" ->
        UnionOfStringLiteralField(unknownField.copy(isArray = false), parseConfigOptionValue(inputNode))

      else -> unknownField
    }
  }

  return null
}
