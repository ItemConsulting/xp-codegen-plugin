package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*
import no.item.xp.plugin.util.OUTPUT_FILE_HEADER_WARNING

fun renderInterfaceModelAsTypeScript(model: InterfaceModel): String {
  val fieldList = model.fields.joinToString("\n\n") { renderInterfaceModelField(it, 1) }

  return """// $OUTPUT_FILE_HEADER_WARNING
    #export interface ${getInterfaceName(model.nameWithoutExtension)} {
    #$fieldList
    #}
    #""".trimMargin("#")
}
