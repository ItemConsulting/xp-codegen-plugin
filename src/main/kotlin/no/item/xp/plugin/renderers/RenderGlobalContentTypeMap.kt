package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getTypeName
import java.io.File

fun renderGlobalContentTypeMap(
  files: List<File>,
  appName: String?,
): String {
  val importList =
    files.joinToString("\n") {
      """export type ${getTypeName(
        it.nameWithoutExtension,
      )} = import("./${it.nameWithoutExtension}").${getTypeName(it.nameWithoutExtension)};"""
    }
  val fieldList =
    files.joinToString("\n") {
      """      "$appName:${it.nameWithoutExtension}": ${getTypeName(it.nameWithoutExtension)};"""
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
