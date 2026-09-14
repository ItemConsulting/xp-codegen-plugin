package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.ObjectField

fun renderObjectField(
  field: ObjectField,
  indentLevel: Int,
  mixinsImportPath: String,
): String {
  val indentation = createIndentation(indentLevel)

  if (field.mixinName != null) {
    val typeReference = renderMixinTypeReference(field.mixinName, mixinsImportPath)
    val fieldType = if (field.isArray) "Array<$typeReference>" else typeReference

    return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}${if (field.isNullable) "?" else ""}: $fieldType;
      """.trimMargin("#")
  }

  val fieldList = field.fields.joinToString("\n\n") { renderInterfaceModelField(it, indentLevel + 1, mixinsImportPath) }

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}${if (field.isNullable) "?" else ""}: ${if (field.isArray) "Array<" else "" }{
      #$fieldList
      #$indentation}${if (field.isArray) ">" else "" };
      """.trimMargin("#")
}
