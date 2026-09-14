package no.item.xp.codegen.parse

import no.item.xp.codegen.form.Input
import no.item.xp.codegen.form.intAt
import no.item.xp.codegen.form.textAt
import no.item.xp.codegen.model.BooleanField
import no.item.xp.codegen.model.Field
import no.item.xp.codegen.model.NumberField
import no.item.xp.codegen.model.NumberFieldWithValidation
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.StringFieldWithValidation
import no.item.xp.codegen.model.UnionOfStringLiteralField

const val REGEX_DATE = "^\\d{4}-([0]\\d|1[0-2])-([0-2]\\d|3[01])\$"
const val REGEX_TIME = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$"

@Suppress("ktlint:standard:max-line-length")
const val REGEX_DATETIME = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?\$"

private val STRING_INPUT_TYPES =
  setOf(
    "htmlarea",
    "geopoint",
    "contentselector",
    "imageselector",
    "mediaselector",
    "attachmentuploader",
    "customselector",
    "tag",
    "contenttypefilter",
    "principalselector",
  )

/**
 * Returns the field for [input], or null if the input has no name or type
 */
fun parseInput(input: Input): Field? {
  val type = input.type ?: return null
  val field = parseUnknownField(input.name, input.label, input.occurrences) ?: return null
  val properties = input.properties

  return when (type.lowercase()) {
    in STRING_INPUT_TYPES ->
      StringField(field)
    "textline",
    "textarea",
    -> {
      val regexp = properties.textAt("regexp")
      val maxLength = properties.intAt("maxLength")

      if (regexp != null || maxLength != null) {
        StringFieldWithValidation(field, regexp, maxLength)
      } else {
        StringField(field)
      }
    }
    "date" ->
      StringFieldWithValidation(field, REGEX_DATE)
    "time" ->
      StringFieldWithValidation(field, REGEX_TIME)
    "datetime",
    "instant",
    ->
      StringFieldWithValidation(field, REGEX_DATETIME)
    "checkbox" ->
      BooleanField(field.copy(isNullable = false, isArray = false))
    "combobox" ->
      UnionOfStringLiteralField(field, parseOptionValues(input))
    "radiobutton" ->
      UnionOfStringLiteralField(field.copy(isArray = false), parseOptionValues(input))
    "long",
    "double",
    -> {
      val min = properties.intAt("min")
      val max = properties.intAt("max")

      if (min != null || max != null) {
        NumberFieldWithValidation(field, min, max)
      } else {
        NumberField(field)
      }
    }
    else -> field
  }
}

fun parseOptionValues(input: Input): List<String> =
  input.properties
    .get("options")
    ?.mapNotNull { it.textAt("value") }
    .orEmpty()
