package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.models.ObjectField
import org.w3c.dom.Node

fun parseItemSet(itemSetNode: Node, mixins: List<ObjectTypeModel>): ObjectField? {
  val unknownField = parseUnknownField(itemSetNode)

  val subFields = itemSetNode.getChildNodeAtXPath("items")
    ?.let { parseFields(it, mixins).getOrNull() } ?: emptyList()

  return unknownField?.let { ObjectField(it, subFields) }
}
