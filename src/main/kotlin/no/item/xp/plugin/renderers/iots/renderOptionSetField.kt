package no.item.xp.plugin.renderers.iots

import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.OptionSetField
import no.item.xp.plugin.renderers.ts.createIndentation
import no.item.xp.plugin.renderers.ts.escapeName
import no.item.xp.plugin.renderers.ts.renderComment

fun renderOptionSetField(field: OptionSetField, indentLevel: Int): String {
  val indentation = createIndentation(indentLevel)
  val options = field.optionList.joinToString(",\n", "t.union([\n", "\n$indentation])") {
    renderOptionSetFieldOption(it, indentLevel + 1)
  }

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}: ${renderNullable(field, renderArray(field, options))},
      """.trimMargin("#")
}

private fun renderOptionSetFieldOption(fieldOption: ObjectField, indentLevel: Int): String {
  val indentation1 = createIndentation(indentLevel)
  val indentation2 = createIndentation(indentLevel + 1)

  val content = fieldOption.fields.joinToString("\n\n") {
    renderInterfaceModelField(
      it,
      indentLevel + 2
    )
  }

  return """
    #${indentation1}t.type({
    #$indentation2/**
    #$indentation2 * Selected
    #$indentation2 */
    #${indentation2}_selected: t.literal("${fieldOption.name}"),
    #
    #${renderComment(fieldOption.comment, indentLevel + 1)}
    #$indentation2${escapeName(fieldOption.name)}: t.type({
    #$content
    #$indentation2})
    #$indentation1})
    """.trimMargin("#")
}
