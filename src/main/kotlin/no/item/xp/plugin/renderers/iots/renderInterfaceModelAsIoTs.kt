package no.item.xp.plugin.renderers.iots

import no.item.xp.plugin.models.*
import no.item.xp.plugin.renderers.ts.getInterfaceName

fun renderInterfaceModelAsIoTs(model: InterfaceModel): String {
  val fieldList = model.fields.joinToString("\n\n") { renderInterfaceModelField(it, 1) }
  val interfaceName = getInterfaceName(model.nameWithoutExtension)
  val addWizardryImport = usesValidation(model.fields)

  return """import * as t from 'io-ts';${if (addWizardryImport) "\nimport * as w from \"enonic-wizardry/validation\";" else ""}
    #
    #export const $interfaceName = t.type({
    #$fieldList
    #});
    #
    #export type $interfaceName = t.TypeOf<typeof $interfaceName>;
    #""".trimMargin("#")
}

fun usesValidation(fields: List<InterfaceModelField>): Boolean {
  return fields.toTypedArray().any { field ->
    when (field) {
      is StringFieldWithValidation,
      is NumberFieldWithValidation -> true
      is ObjectField -> usesValidation(field.fields)
      is OptionSetField -> usesValidation(field.optionList.flatMap { it.fields })
      else -> false
    }
  }
}
