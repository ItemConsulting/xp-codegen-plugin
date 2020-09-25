package no.item.xp.plugin.parser

import arrow.core.orNull
import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.InterfaceModel
import no.item.xp.plugin.models.ObjectField
import org.w3c.dom.Node

fun parseItemSet(itemSetNode: Node, mixins: List<InterfaceModel>): ObjectField? {
  val unknownField = parseUnknownField(itemSetNode)

  val subFields = itemSetNode.getChildNodeAtXPath("items")
    ?.let { parseFields(it, mixins).orNull() } ?: emptyList()

  return unknownField?.let { ObjectField(it, subFields) }
}
