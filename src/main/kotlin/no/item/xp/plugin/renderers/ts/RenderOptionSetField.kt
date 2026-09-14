package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.OptionSetField

fun renderOptionSetField(
  field: OptionSetField,
  indentLevel: Int,
  mixinsImportPath: String,
): String {
  val indentation = createIndentation(indentLevel)
  val options = field.optionList.joinToString("\n") { renderOptionSetFieldOption(it, indentLevel + 2, mixinsImportPath) }

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}${if (field.isNullable) "?" else ""}:${if (field.isArray) " Array<" else "" }
      #$options${if (field.isArray) "\n$indentation>" else "" };
      """.trimMargin("#")
}

private fun renderOptionSetFieldOption(
  fieldOption: ObjectField,
  indentLevel: Int,
  mixinsImportPath: String,
): String {
  val indentation0 = createIndentation(indentLevel - 1)
  val indentation1 = createIndentation(indentLevel)
  val indentation2 = createIndentation(indentLevel + 1)
  val content = fieldOption.fields.joinToString("\n\n") { renderInterfaceModelField(it, indentLevel + 2, mixinsImportPath) }

  val optionType =
    when {
      fieldOption.mixinName != null -> renderMixinTypeReference(fieldOption.mixinName, mixinsImportPath) + ";"
      fieldOption.fields.isNotEmpty() -> "{\n$content\n#$indentation2};"
      else -> "Record<string, unknown>;"
    }

  return """
    #$indentation0| {
    #$indentation2/**
    #$indentation2 * Selected
    #$indentation2 */
    #${indentation2}_selected: "${fieldOption.name}";
    #
    #${renderComment(fieldOption.comment, indentLevel + 1)}
    #$indentation2${escapeName(fieldOption.name)}: $optionType
    #$indentation1}
    """.trimMargin("#")
}
