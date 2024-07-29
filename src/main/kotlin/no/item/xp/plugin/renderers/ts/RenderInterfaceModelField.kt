package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.BooleanField
import no.item.xp.plugin.models.NumberField
import no.item.xp.plugin.models.NumberFieldWithValidation
import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.ObjectTypeModelField
import no.item.xp.plugin.models.OptionSetField
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.models.StringFieldWithValidation
import no.item.xp.plugin.models.UnionOfStringLiteralField
import no.item.xp.plugin.models.UnknownField

fun renderInterfaceModelField(
  field: ObjectTypeModelField,
  indent: Int,
): String {
  return when (field) {
    is StringField,
    is StringFieldWithValidation,
    -> simpleFieldAsString(field, "string", indent)
    is NumberFieldWithValidation,
    is NumberField,
    -> simpleFieldAsString(field, "number", indent)
    is BooleanField -> simpleFieldAsString(field, "boolean", indent)
    is UnknownField -> simpleFieldAsString(field, "unknown", indent)
    is UnionOfStringLiteralField -> simpleFieldAsString(field, joinOptionList(field.optionList), indent)
    is OptionSetField ->
      if (field.isMultiSelect) {
        renderOptionSetFieldMultiChoice(field, indent)
      } else {
        renderOptionSetField(field, indent)
      }
    is ObjectField -> renderObjectField(field, indent)
  }
}

private fun simpleFieldAsString(
  field: ObjectTypeModelField,
  innerType: String,
  indentLevel: Int,
): String {
  val indentation = createIndentation(indentLevel)

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(
    field.name,
  )}${if (field.isNullable) "?" else ""}: ${if (field.isArray) "Array<$innerType> | $innerType" else innerType};
      """.trimMargin("#")
}
