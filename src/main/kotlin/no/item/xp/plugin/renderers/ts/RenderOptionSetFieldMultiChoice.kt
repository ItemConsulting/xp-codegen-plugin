package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.OptionSetField

fun renderOptionSetFieldMultiChoice(
  field: OptionSetField,
  indentLevel: Int,
  mixinsImportPath: String,
): String {
  val indentation0 = createIndentation(indentLevel)
  val indentation1 = createIndentation(indentLevel + 1)

  val optionNames = field.optionList.joinToString(" | ") { "\"${it.name}\"" }

  val options =
    field.optionList.joinToString("\n$indentation1\n") {
      renderMultiSelectOptionSetFieldOption(it, indentLevel, mixinsImportPath)
    }

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation0${escapeName(field.name)}${if (field.isNullable) "?" else ""}: ${if (field.isArray) "Array<" else "" }{
      #$indentation1/**
      #$indentation1 * Selected
      #$indentation1 */
      #${indentation1}_selected: Array<$optionNames>;
      #$indentation1
      #$options
      #$indentation0}${if (field.isArray) ">" else "" };
      """.trimMargin("#")
}

private fun renderMultiSelectOptionSetFieldOption(
  fieldOption: ObjectField,
  indentLevel: Int,
  mixinsImportPath: String,
): String {
  val indentation = createIndentation(indentLevel + 1)
  val content = fieldOption.fields.joinToString("\n\n") { renderInterfaceModelField(it, indentLevel + 2, mixinsImportPath) }

  val optionType =
    when {
      fieldOption.mixinName != null -> renderMixinTypeReference(fieldOption.mixinName, mixinsImportPath)
      fieldOption.fields.isNotEmpty() -> "{\n$content\n#$indentation}"
      else -> "Record<string, unknown>"
    }

  return """
    #${renderComment(fieldOption.comment, indentLevel + 1)}
    #$indentation${escapeName(fieldOption.name)}: $optionType;
    """.trimMargin("#")
}
