package no.item.xp.plugin.renderers.iots

import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.renderers.ts.createIndentation
import no.item.xp.plugin.renderers.ts.escapeName
import no.item.xp.plugin.renderers.ts.renderComment

fun renderObjectField(field: ObjectField, indentLevel: Int): String {
  val indentation = createIndentation(indentLevel)

  val fieldList = field.fields.joinToString("\n\n") { renderInterfaceModelField(it, indentLevel + 1) }

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}: ${renderNullable(field, renderArray(field, fieldList))}
      """.trimMargin("#")
}
