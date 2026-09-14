package no.item.xp.plugin.parser

import no.item.xp.plugin.models.BooleanField
import no.item.xp.plugin.models.NumberField
import no.item.xp.plugin.models.NumberFieldWithValidation
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.models.UnknownField

/**
 * The params of a macro are passed in from the HtmlArea, so they are always strings
 */
fun toMacroModel(model: ObjectTypeModel): ObjectTypeModel =
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
