package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getTypeName

fun renderGlobalXDataMap(
  fileNames: List<String>,
  appName: String?,
): String {
  val importList =
    fileNames.joinToString("\n") { fileName ->
      """export type ${getTypeName(fileName)} = import("./$fileName").${getTypeName(fileName)}"""
    }
  val fieldList =
    fileNames.joinToString("\n") { fileName ->
      """      ${escapeFieldName(fileName)}?: ${getTypeName(fileName)};"""
    }

  return if (appName == null) {
    importList
  } else {
    """
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
}

fun snakeCase(str: String) = str.replace(".", "-")

fun escapeFieldName(fieldName: String) = if (fieldName.contains("-")) """"$fieldName"""" else fieldName
