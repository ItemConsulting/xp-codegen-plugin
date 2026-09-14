package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getTypeName

fun renderGlobalContentTypeMap(
  names: List<String>,
  appName: String?,
): String {
  val importList =
    names.joinToString("\n") { name ->
      """export type ${getTypeName(name)} = import("./$name").${getTypeName(name)};"""
    }
  val fieldList =
    names.joinToString("\n") { name ->
      """      "$appName:$name": ${getTypeName(name)};"""
    }

  return if (appName == null) {
    importList
  } else {
    """
    #$importList
    #
    #declare global {
    #  namespace XP {
    #    interface ContentTypes {
    #$fieldList
    #    }
    #  }
    #}
    #""".trimMargin("#")
  }
}
