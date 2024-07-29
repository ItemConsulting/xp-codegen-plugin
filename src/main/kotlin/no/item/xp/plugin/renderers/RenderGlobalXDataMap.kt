package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getInterfaceName

fun renderGlobalXDataMap(
  fileNames: List<String>,
  appName: String,
): String {
  val importList =
    fileNames.joinToString("\n") { fileName ->
      """export type ${getInterfaceName(fileName)} = import("./$fileName").${getInterfaceName(fileName)}"""
    }
  val fieldList =
    fileNames.joinToString("\n") { fileName ->
      """      ${escapeFieldName(fileName)}?: ${getInterfaceName(fileName)};"""
    }

  return """
    #$importList
    #
    #declare global {
    #  interface XpXData {
    #    "${snakeCase(appName)}"?: {
    #$fieldList
    #    }
    #  }
    #}
    #""".trimMargin("#")
}

fun snakeCase(str: String) = str.replace(".", "-")

fun escapeFieldName(fieldName: String) = if (fieldName.contains("-")) """"$fieldName"""" else fieldName
