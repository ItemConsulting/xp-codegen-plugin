package no.item.xp.codegen.parse

import no.item.xp.codegen.model.BooleanField
import no.item.xp.codegen.model.NumberField
import no.item.xp.codegen.model.NumberFieldWithValidation
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.TypeModel
import no.item.xp.codegen.model.UnknownField

/**
 * The params of a macro are passed in from the HtmlArea, so they are always strings
 */
fun toMacroModel(model: TypeModel): TypeModel =
  model.copy(
    fields =
      model.fields.map { field ->
        when (field) {
          is NumberField,
          is NumberFieldWithValidation,
          is BooleanField,
          is UnknownField,
          -> StringField(field)
          else -> field
        }
      },
  )
