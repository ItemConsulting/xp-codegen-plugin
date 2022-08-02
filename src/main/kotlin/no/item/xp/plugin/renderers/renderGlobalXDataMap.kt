package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getInterfaceName
import java.io.File

fun renderGlobalXDataMap(files: List<File>, appName: String): String {
  val importList = files.joinToString("\n") {
    """export type ${getInterfaceName(it.nameWithoutExtension)} = import("./${it.nameWithoutExtension}").${getInterfaceName(it.nameWithoutExtension)}"""
  }
  val fieldList = files.joinToString("\n") {
    """        ${escapeFieldName(it.nameWithoutExtension)}?: ${getInterfaceName(it.nameWithoutExtension)};"""
  }

  return """
    #$importList
    #
    #declare global {
    #  namespace XP {
    #    interface XData {
    #      "${snakeCase(appName)}"?: {
    #$fieldList
    #      }
    #    }
    #  }
    #}
    #""".trimMargin("#")
}

fun snakeCase(str: String) = str.replace(".", "-")

fun escapeFieldName(fieldName: String) =
  if (fieldName.contains("-")) """"$fieldName"""" else fieldName
