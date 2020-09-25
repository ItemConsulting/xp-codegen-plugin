package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.ObjectField

fun renderObjectField(field: ObjectField, indentLevel: Int): String {
  val indentation = createIndentation(indentLevel)

  val fieldList = field.fields.joinToString("\n\n") { renderInterfaceModelField(it, indentLevel + 1) }

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}${if (field.nullable) "?" else ""}: ${if (field.isArray) "Array<" else "" }{
      #$fieldList
      #$indentation}${if (field.isArray) ">" else "" };
      """.trimMargin("#")
}
