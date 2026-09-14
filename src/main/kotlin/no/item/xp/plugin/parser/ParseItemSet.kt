package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.ObjectTypeModel
import org.w3c.dom.Node

fun parseItemSet(
  itemSetNode: Node,
  mixins: List<ObjectTypeModel>,
): ObjectField? {
  val unknownField = parseUnknownField(itemSetNode)
  val itemsNode = itemSetNode.getChildNodeAtXPath("items")

  val subFields =
    itemsNode
      ?.let { parseFields(it, mixins).getOrNull() } ?: emptyList()

  val mixinName = itemsNode?.let { findSingleMixinName(it, mixins) }

  return unknownField?.let { ObjectField(it, subFields, mixinName) }
}
