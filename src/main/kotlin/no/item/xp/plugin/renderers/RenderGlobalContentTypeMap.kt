package no.item.xp.plugin.renderers

import no.item.xp.plugin.renderers.ts.getInterfaceName
import java.io.File

fun renderGlobalContentTypeMap(
  files: List<File>,
  appName: String,
): String {
  val importList =
    files.joinToString("\n") {
      """export type ${getInterfaceName(
        it.nameWithoutExtension,
      )} = import("./${it.nameWithoutExtension}").${getInterfaceName(it.nameWithoutExtension)};"""
    }
  val fieldList =
    files.joinToString("\n") {
      """      "$appName:${it.nameWithoutExtension}": ${getInterfaceName(it.nameWithoutExtension)};"""
    }

  return """
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
