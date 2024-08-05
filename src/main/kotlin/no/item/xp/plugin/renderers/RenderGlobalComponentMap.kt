package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getTypeName

fun renderGlobalComponentMap(
  filesNames: List<String>,
  appName: String?,
  interfaceName: String?,
): String {
  val importList =
    filesNames.joinToString("\n") { fileName ->
      """export type ${getTypeName(fileName)} = import("./$fileName").${getTypeName(fileName)};"""
    } + "\n"

  return if (interfaceName == null || appName == null) {
    importList
  } else {
    val fieldList =
      filesNames.joinToString("\n") { fileName ->
        """    "$appName:$fileName": ${getTypeName(fileName)};"""
      }

    """
    #$importList
    #declare global {
    #  interface $interfaceName {
    #$fieldList
    #  }
    #}
    #""".trimMargin("#")
  }
}
