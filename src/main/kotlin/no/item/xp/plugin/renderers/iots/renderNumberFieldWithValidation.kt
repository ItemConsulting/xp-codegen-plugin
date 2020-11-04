package no.item.xp.plugin.renderers.iots

import no.item.xp.plugin.models.NumberFieldWithValidation

fun renderNumberFieldWithValidation(field: NumberFieldWithValidation, indentLevel: Int): String {
  return if (field.min != null && field.max != null) {
    simpleFieldAsString(field, "w.MinMaxValidatedNumber({ min: ${field.min}, max: ${field.max} })", indentLevel)
  } else if (field.min != null) {
    simpleFieldAsString(field, "w.MinMaxValidatedNumber({ min: ${field.min} })", indentLevel)
  } else {
    simpleFieldAsString(field, "w.MinMaxValidatedNumber({ max: ${field.max} })", indentLevel)
  }
}
