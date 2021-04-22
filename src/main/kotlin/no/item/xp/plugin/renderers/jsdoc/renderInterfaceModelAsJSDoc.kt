package no.item.xp.plugin.renderers.jsdoc

import no.item.xp.plugin.models.*
import no.item.xp.plugin.renderers.ts.getInterfaceName

fun renderInterfaceModelAsJSDoc(model: InterfaceModel): String {
  val fieldList = model.fields.joinToString("\n") { renderInterfaceModelField(it, emptyList()) }

  return """
    #/**
    # * @typedef {Object} ${getInterfaceName(model.nameWithoutExtension)}
    #$fieldList
    # */
    #""".trimMargin("#")
}
