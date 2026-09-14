package no.item.xp.codegen.render

import no.item.xp.codegen.model.BooleanField
import no.item.xp.codegen.model.Field
import no.item.xp.codegen.model.NumberField
import no.item.xp.codegen.model.NumberFieldWithValidation
import no.item.xp.codegen.model.ObjectField
import no.item.xp.codegen.model.OptionSetField
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.StringFieldWithValidation
import no.item.xp.codegen.model.TypeModel
import no.item.xp.codegen.model.UnionOfStringLiteralField
import no.item.xp.codegen.model.UnknownField

/**
 * Renders [model] as an exported TypeScript type
 */
fun renderTypeModel(
  model: TypeModel,
  fragmentsImportPath: String,
  indentUnit: String = DEFAULT_INDENT_UNIT,
): String =
  CodeWriter(indentUnit)
    .apply { objectType("export type ${getTypeName(model.name)} = ", ";", model.fields, fragmentsImportPath) }
    .toString()

/**
 * Renders [model] as the global "XP.SiteConfig" interface
 */
fun renderSiteConfig(
  model: TypeModel,
  fragmentsImportPath: String,
  indentUnit: String = DEFAULT_INDENT_UNIT,
): String =
  CodeWriter(indentUnit)
    .apply {
      line("export type SiteConfig = XP.SiteConfig;")
      line()
      block("declare global {", "}") {
        block("namespace XP {", "}") {
          if (model.fields.isEmpty()) {
            line("interface SiteConfig {}")
          } else {
            block("interface SiteConfig {", "}") {
              fields(model.fields, fragmentsImportPath)
            }
          }
        }
      }
    }.toString()

fun renderFragmentReference(
  fragmentName: String,
  fragmentsImportPath: String,
): String = "import(\"$fragmentsImportPath/$fragmentName\").${getTypeName(fragmentName)}"

fun CodeWriter.fields(
  fields: List<Field>,
  fragmentsImportPath: String,
): CodeWriter = separated(fields) { field(it, fragmentsImportPath) }

fun CodeWriter.field(
  field: Field,
  fragmentsImportPath: String,
): CodeWriter =
  apply {
    comment(field.comment)

    when (field) {
      is StringField,
      is StringFieldWithValidation,
      -> simpleField(field, "string")
      is NumberField,
      is NumberFieldWithValidation,
      -> simpleField(field, "number")
      is BooleanField -> simpleField(field, "boolean")
      is UnknownField -> simpleField(field, "unknown")
      is UnionOfStringLiteralField -> simpleField(field, joinOptionList(field.optionList))
      is ObjectField -> objectField(field, fragmentsImportPath)
      is OptionSetField ->
        if (field.isMultiSelect) {
          multiSelectOptionSetField(field, fragmentsImportPath)
        } else {
          optionSetField(field, fragmentsImportPath)
        }
    }
  }

/**
 * Writes an object type with [fields], or "Record<string, never>" if there are no fields
 */
private fun CodeWriter.objectType(
  prefix: String,
  suffix: String,
  fields: List<Field>,
  fragmentsImportPath: String,
) {
  if (fields.isEmpty()) {
    line("${prefix}Record<string, never>$suffix")
  } else {
    block("$prefix{", "}$suffix") { fields(fields, fragmentsImportPath) }
  }
}

// E.g. "name?:"
private fun declaration(field: Field): String = "${escapeName(field.name)}${if (field.isNullable) "?" else ""}:"

private fun CodeWriter.simpleField(
  field: Field,
  type: String,
) {
  line("${declaration(field)} ${if (field.isArray) "Array<$type> | $type" else type};")
}

private fun CodeWriter.objectField(
  field: ObjectField,
  fragmentsImportPath: String,
) {
  if (field.fragmentName != null) {
    val type = renderFragmentReference(field.fragmentName, fragmentsImportPath)
    line("${declaration(field)} ${if (field.isArray) "Array<$type>" else type};")
  } else if (field.isArray) {
    objectType("${declaration(field)} Array<", ">;", field.fields, fragmentsImportPath)
  } else {
    objectType("${declaration(field)} ", ";", field.fields, fragmentsImportPath)
  }
}

/**
 * Writes a union of the options. The "| " prefix takes one level of indentation, so the content of each option is
 * indented by two more levels.
 */
private fun CodeWriter.optionSetField(
  field: OptionSetField,
  fragmentsImportPath: String,
) {
  if (field.optionList.isEmpty()) {
    simpleField(field, "never")
    return
  }

  line(if (field.isArray) "${declaration(field)} Array<" else declaration(field))

  indent {
    field.optionList.forEachIndexed { index, option ->
      line("| {")
      indent(2) {
        comment("Selected")
        line("_selected: \"${option.name}\";")
        line()
        comment(option.comment)
        optionValue(option, fragmentsImportPath)
      }
      indent {
        line(if (index == field.optionList.lastIndex && !field.isArray) "};" else "}")
      }
    }
  }

  if (field.isArray) {
    line(">;")
  }
}

private fun CodeWriter.multiSelectOptionSetField(
  field: OptionSetField,
  fragmentsImportPath: String,
) {
  val open = if (field.isArray) "${declaration(field)} Array<{" else "${declaration(field)} {"
  val close = if (field.isArray) "}>;" else "};"

  block(open, close) {
    comment("Selected")
    line("_selected: Array<${joinOptionList(field.optionList.map { it.name })}>;")

    field.optionList.forEach { option ->
      line()
      comment(option.comment)
      optionValue(option, fragmentsImportPath)
    }
  }
}

private fun CodeWriter.optionValue(
  option: ObjectField,
  fragmentsImportPath: String,
) {
  val name = escapeName(option.name)

  when {
    option.fragmentName != null -> line("$name: ${renderFragmentReference(option.fragmentName, fragmentsImportPath)};")
    option.fields.isNotEmpty() -> objectType("$name: ", ";", option.fields, fragmentsImportPath)
    else -> line("$name: Record<string, unknown>;")
  }
}
