package no.item.xp.plugin.writer

import java.io.BufferedWriter
import java.io.File
import no.item.xp.plugin.models.*

fun typeScriptWriter(generatedTypeScriptInterfaceFile: String, type: XmlType, objList: List<GeneratedInputType>): String {
  var content = "export interface "
  content += xmlTypeFormatted(type) + "{\n"
  for (fields: GeneratedInputType in objList) {
    content += returnCommentFromLabel(fields)
    content += returnStringValue(fields) + "\n"
    content += "\n"
  }
  content += "}"
  File(generatedTypeScriptInterfaceFile).bufferedWriter().use { out: BufferedWriter ->
    out.write(content)
  }
  return generatedTypeScriptInterfaceFile
}

fun returnStringValue(fields: GeneratedInputType): String {
  val name: String = fields.name
  val nullable: String = isNullable(fields.nullable)
  val separator = ": "
  val type: String = typeScriptType(fields)
  val end = ";"
  return "$name$nullable$separator$type$end"
}

fun typeScriptType(fields: GeneratedInputType): String {
  return when (fields) {
    is UnionOfStringField -> showUnion(fields.optionList)
    is StringField -> "string"
    is BooleanField -> "boolean"
    is MultipleField -> "Array<string>"
  }
}
fun showUnion(optionList: Sequence<String>): String {
  var returnValue = ""
  optionList.forEach {
    returnValue += "\"" + it + "\""
    if (it != optionList.last() && optionList.count()> 1) {
      returnValue += " | "
    }
  }
  return returnValue
}

fun isNullable(valueIsNullable: Boolean): String =
  if (valueIsNullable) "?" else ""

fun xmlTypeFormatted(type: XmlType): String {
  return when (type) {
    XmlType.SITE -> "Site"
    XmlType.ID_PROVIDER -> "IdProvider"
    XmlType.CONTENT_TYPE -> "ContentType"
    XmlType.LAYOUT -> "Layout"
    XmlType.PART -> "Part"
    XmlType.PAGE -> "Page"
    XmlType.MACRO -> "Macro"
    XmlType.MIXIN -> "Mixin"
  }
}

fun returnCommentFromLabel(fields: GeneratedInputType): String =
  fields.comment
    .filter { it.isNotBlank() }
    .fold(
      { "\n" },
      { "/** $it */\n" }
    )
