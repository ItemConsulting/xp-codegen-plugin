package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.extensions.getNodeAttribute
import no.item.xp.plugin.models.*
import org.w3c.dom.Node

fun parseInput(inputNode: Node): InterfaceModelField? {
  val unknownField = parseUnknownField(inputNode)
  val type = inputNode.getNodeAttribute("type")

  if (unknownField != null && type != null) {
    return when (type.toLowerCase()) {
      "date",
      "time",
      "datetime",
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
