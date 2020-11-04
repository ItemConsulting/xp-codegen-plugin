package no.item.xp.plugin.renderers.jsdoc

import no.item.xp.plugin.models.*
import no.item.xp.plugin.renderers.ts.joinOptionList

fun renderInterfaceModelField(field: InterfaceModelField, path: List<String>): String {
  return when (field) {
    is StringField,
    is StringFieldWithValidation -> simpleJsDocFieldAsString(field, "string", path)
    is NumberFieldWithValidation,
    is NumberField -> simpleJsDocFieldAsString(field, "number", path)
    is BooleanField -> simpleJsDocFieldAsString(field, "boolean", path)
    is UnknownField -> simpleJsDocFieldAsString(field, "unknown", path)
    is UnionOfStringLiteralField -> simpleJsDocFieldAsString(field, joinOptionList(field.optionList, "(", ")"), path)
    is OptionSetField -> renderOptionSetField(field, path)
    is ObjectField -> {
      simpleJsDocFieldAsString(field, "Object", path) + "\n" +
        field.fields.joinToString("\n") { renderInterfaceModelField(it, path.plus(field.name)) }
    }
  }
}
