package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getInterfaceName

fun renderGlobalComponentMap(
  filesNames: List<String>,
  appName: String?,
  interfaceName: String?,
): String {
  val importList =
    filesNames.joinToString("\n") { fileName ->
      """export type ${getInterfaceName(fileName)} = import("./$fileName").${getInterfaceName(fileName)};"""
    }

  return if (interfaceName == null || appName == null) {
    importList
  } else {
    val fieldList =
      filesNames.joinToString("\n") { fileName ->
        """    "$appName:$fileName": ${getInterfaceName(fileName)};"""
      }

    """
    #$importList
    #
    #declare global {
    #  interface $interfaceName {
    #$fieldList
    #  }
    #}
    #""".trimMargin("#")
  }
}
