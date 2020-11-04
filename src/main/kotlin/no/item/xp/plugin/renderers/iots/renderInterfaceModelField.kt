package no.item.xp.plugin.renderers.iots

import no.item.xp.plugin.models.*

fun renderInterfaceModelField(field: InterfaceModelField, indent: Int): String {
  return when (field) {
    is StringField -> simpleFieldAsString(field, "t.string", indent)
    is StringFieldWithValidation -> renderStringFieldWithValidation(field, indent)
    is NumberField -> simpleFieldAsString(field, "t.number", indent)
    is NumberFieldWithValidation -> renderNumberFieldWithValidation(field, indent)
    is BooleanField -> simpleFieldAsString(field, "t.boolean", indent)
    is UnknownField -> simpleFieldAsString(field, "t.unknown", indent)
    is UnionOfStringLiteralField -> simpleFieldAsString(field, joinOptionList(field.optionList), indent)
    is OptionSetField -> renderOptionSetField(field, indent)
    is ObjectField -> renderObjectField(field, indent)
  }
}
