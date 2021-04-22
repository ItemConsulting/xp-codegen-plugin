package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.OptionSetField

fun renderOptionSetField(field: OptionSetField, indentLevel: Int): String {
  val indentation = createIndentation(indentLevel)
  val options = field.optionList.joinToString("\n") { renderOptionSetFieldOption(it, indentLevel + 2) }

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}${if (field.isNullable) "?" else ""}:${if (field.isArray) " Array<" else "" }
      #$options${if (field.isArray) "\n$indentation>" else "" };
      """.trimMargin("#")
}

private fun renderOptionSetFieldOption(fieldOption: ObjectField, indentLevel: Int): String {
  val indentation0 = createIndentation(indentLevel - 1)
  val indentation1 = createIndentation(indentLevel)
  val indentation2 = createIndentation(indentLevel + 1)
  val content = fieldOption.fields.joinToString("\n\n") { renderInterfaceModelField(it, indentLevel + 2) }

  return """
    #$indentation0| {
    #$indentation2/**
    #$indentation2 * Selected
    #$indentation2 */
    #${indentation2}_selected: "${fieldOption.name}";
    #
    #${renderComment(fieldOption.comment, indentLevel + 1)}
    #$indentation2${escapeName(fieldOption.name)}: ${if (fieldOption.fields.isNotEmpty()) "{\n$content" else "Record<string, unknown>;"}${if (fieldOption.fields.isNotEmpty()) "\n#$indentation2};" else ""}
    #$indentation1}
    """.trimMargin("#")
}
