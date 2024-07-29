package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getInterfaceName

fun renderGlobalComponentMap(filesNames: List<String>, appName: String, interfaceName: String): String {
  val importList = filesNames.joinToString("\n") { fileName ->
    """export type ${getInterfaceName(fileName)} = import("./${fileName}").${getInterfaceName(fileName)};"""
  }
  val fieldList = filesNames.joinToString("\n") { fileName ->
    """    "$appName:${fileName}": ${getInterfaceName(fileName)};"""
  }

  return """
    #$importList
    #
    #declare global {
    #  interface $interfaceName {
    #$fieldList
    #  }
    #}
    #""".trimMargin("#")
}
