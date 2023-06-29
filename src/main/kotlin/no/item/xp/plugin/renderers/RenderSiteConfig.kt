package no.item.xp.plugin.renderers

import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.renderers.ts.renderInterfaceModelField

fun renderSiteConfig(model: ObjectTypeModel): String {
  val fieldList = model.fields.joinToString("\n\n") { renderInterfaceModelField(it, 3) }

  return """
    #export type SiteConfig = XP.SiteConfig;
    #
    #declare global {
    #  namespace XP {
    #    interface SiteConfig {
    #$fieldList
    #    }
    #  }
    #}
    #""".trimMargin("#")
}
