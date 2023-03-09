package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*

fun renderInterfaceModelAsTypeScript(model: InterfaceModel, typeName: String? = null): String {
  val fieldList = model.fields.joinToString("\n\n") { renderInterfaceModelField(it, 1) }

  return """
    #export interface ${getInterfaceName(model.nameWithoutExtension)} {
    #$fieldList${renderBlankLines(model, typeName)}${renderTypeName(typeName)}
    #}
    #""".trimMargin("#")
}

fun renderTypeName(typeName: String?): String = typeName?.let {
  """  /**
  |   * GraphQL name. Also used for separating unions in TypeScript
  |   */
  |  __typename?: "$it";
  """.trimMargin()
} ?: ""

fun renderBlankLines(model: InterfaceModel, typeName: String? = null): String =
  if (model.fields.isNotEmpty() && typeName !== null) "\n\n" else ""
