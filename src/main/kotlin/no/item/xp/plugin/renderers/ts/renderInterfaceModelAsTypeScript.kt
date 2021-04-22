package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*

fun renderInterfaceModelAsTypeScript(model: InterfaceModel): String {
  val fieldList = model.fields.joinToString("\n\n") { renderInterfaceModelField(it, 1) }

  return """
    #export interface ${getInterfaceName(model.nameWithoutExtension)} {
    #$fieldList
    #}
    #""".trimMargin("#")
}
