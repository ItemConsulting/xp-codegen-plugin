package no.item.xp.plugin.renderers.jsdoc

import no.item.xp.plugin.models.*
import no.item.xp.plugin.renderers.ts.getInterfaceName
import no.item.xp.plugin.util.OUTPUT_FILE_HEADER_WARNING

fun renderInterfaceModelAsJSDoc(model: InterfaceModel): String {
  val fieldList = model.fields.joinToString("\n") { renderInterfaceModelField(it, emptyList()) }

  return """// $OUTPUT_FILE_HEADER_WARNING
    #/**
    # * @typedef {Object} ${getInterfaceName(model.nameWithoutExtension)}
    #$fieldList
    # */
    #""".trimMargin("#")
}
