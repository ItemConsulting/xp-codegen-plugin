package no.item.xp.plugin.renderers.jsdoc

import no.item.xp.plugin.models.OptionSetField
import no.item.xp.plugin.models.UnionOfStringLiteralField
import no.item.xp.plugin.renderers.ts.joinOptionList

fun renderOptionSetField(field: OptionSetField, path: List<String>): String {
  val selectedField = UnionOfStringLiteralField(
    "_selected",
    "Selected",
    false,
    false,
    field.optionList.map { it.name }
  )

  val optionObjects = field.optionList
    .joinToString("") { optionSetFieldOption ->
      renderInterfaceModelField(optionSetFieldOption, path.plus(field.name))
    }

  return """${simpleJsDocFieldAsString(field, "Object", path)}
    #${simpleJsDocFieldAsString(selectedField, joinOptionList(selectedField.optionList, "(", ")"), path.plus(field.name))}
    #$optionObjects"""
}
