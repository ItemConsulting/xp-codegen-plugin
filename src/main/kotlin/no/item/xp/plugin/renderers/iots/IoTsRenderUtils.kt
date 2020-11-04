package no.item.xp.plugin.renderers.iots

import no.item.xp.plugin.models.InterfaceModelField
import no.item.xp.plugin.renderers.ts.createIndentation
import no.item.xp.plugin.renderers.ts.escapeName
import no.item.xp.plugin.renderers.ts.renderComment

fun simpleFieldAsString(field: InterfaceModelField, innerType: String, indentLevel: Int): String {
  val indentation = createIndentation(indentLevel)

  return """
      #${renderComment(field.comment, indentLevel)}
      #$indentation${escapeName(field.name)}: ${renderNullable(field, renderArray(field, innerType))},
      """.trimMargin("#")
}

fun renderNullable(field: InterfaceModelField, innerType: String) =
  if (field.isNullable) "t.union([t.undefined, $innerType])" else innerType

fun renderArray(field: InterfaceModelField, innerType: String) =
  if (field.isArray) "t.array($innerType)" else innerType

fun joinOptionList(optionList: List<String>): String =
  optionList.joinToString(", ", "t.union([", "])") { "t.literal(\"$it\")" }
