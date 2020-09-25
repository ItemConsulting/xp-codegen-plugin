package no.item.xp.plugin.parser

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
      "textarea",
      "textline",
      "contentselector",
      "imageselector",
      "contenttypefilter",
      "mediaselector",
      "attachmentuploader",
      "customselector",
      "tag" ->
        StringField(unknownField)
      "checkbox" ->
        BooleanField(unknownField.copy(nullable = false, isArray = false))
      "combobox" ->
        UnionOfStringLiteralField(unknownField, parseConfigOptionValue(inputNode))
      "long",
      "double" ->
        NumberField(unknownField)
      "radiobutton" ->
        UnionOfStringLiteralField(unknownField.copy(isArray = false), parseConfigOptionValue(inputNode))

      else -> unknownField
    }
  }

  return null
}
