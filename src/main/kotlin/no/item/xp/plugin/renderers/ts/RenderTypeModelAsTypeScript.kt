package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*

fun renderTypeModelAsTypeScript(model: ObjectTypeModel): String {
  val fieldList = model.fields.joinToString("\n\n") { renderInterfaceModelField(it, 1) }

  return """
    #export type ${getInterfaceName(model.nameWithoutExtension)} = {
    #$fieldList
    #}
    #""".trimMargin("#")
}
