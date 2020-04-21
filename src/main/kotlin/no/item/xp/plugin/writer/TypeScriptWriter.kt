package no.item.xp.plugin.writer

import java.io.BufferedWriter
import java.io.File
import no.item.xp.plugin.models.GeneratedField
import no.item.xp.plugin.models.InputType
import no.item.xp.plugin.models.XmlType

@Suppress("FunctionName")
fun TypeScriptWriter(generatedTypeScriptInterfaceFile: String, type: XmlType, objList: List<Any>): String {
  var content = "export interface "
  content += xmlTypeFormatted(type) + "{\n"
  for (fields: Any in objList) {
    returnCommentFromAny(fields)
    if (fields is GeneratedField) {
      content += returnCommentFromLabel(fields)
      content += returnStringValue(fields) + "\n"
      content += "\n"
    }
  }
  content += "}"
  File(generatedTypeScriptInterfaceFile).bufferedWriter().use { out: BufferedWriter ->
    out.write(content)
  }
  return generatedTypeScriptInterfaceFile
}

fun returnStringValue(fields: GeneratedField): String {
  val name: String = fields.name
  val nullable: String = isNullable(fields.nullable)
  val separator = ": "
  val type: String = typeScriptType(fields.type)
  val end = ";"
  return "$name$nullable$separator$type$end"
}

fun typeScriptType(type: InputType): String {
  return when (type) {
    InputType.CHECKBOX -> "boolean"
    InputType.DOUBLE -> "number"
    InputType.LONG -> "number"
    else -> "string"
  }
}

fun isNullable(valueIsNullable: Boolean): String{
  return if (valueIsNullable) "?" else ""
}

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

fun returnCommentFromAny(fields: Any):String{
  println(fields.javaClass)
  return ""
}

fun returnCommentFromLabel(fields: GeneratedField): String
  = fields.comment
    .filter { it.isNotBlank() }
    .fold(
      {"\n"},
      { "/** $it */\n" }
    )

