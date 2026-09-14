package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.ObjectTypeModel

fun renderTypeModelAsTypeScript(
  model: ObjectTypeModel,
  mixinsImportPath: String,
): String {
  val fieldList = model.fields.joinToString("\n\n") { renderInterfaceModelField(it, 1, mixinsImportPath) }

  return """
    #export type ${getTypeName(model.nameWithoutExtension)} = {
    #$fieldList
    #};
    #""".trimMargin("#")
}
