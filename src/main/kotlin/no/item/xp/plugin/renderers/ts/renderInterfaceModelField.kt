package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*

fun renderInterfaceModelField(field: InterfaceModelField, indent: Int): String {
  return when (field) {
    is StringField -> simpleFieldAsString(field, "string", indent)
    is NumberField -> simpleFieldAsString(field, "number", indent)
    is BooleanField -> simpleFieldAsString(field, "boolean", indent)
    is UnknownField -> simpleFieldAsString(field, "unknown", indent)
    is UnionOfStringLiteralField -> simpleFieldAsString(field, joinOptionList(field.optionList), indent)
    is OptionSetField -> renderOptionSetField(field, indent)
    is ObjectField -> renderObjectField(field, indent)
  }
}

private fun simpleFieldAsString(field: InterfaceModelField, innerType: String, indentLevel: Int): String {
  val indentation = createIndentation(indentLevel)

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}${if (field.nullable) "?" else ""}: ${if (field.isArray) "Array<$innerType>" else innerType};
      """.trimMargin("#")
}