package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.InterfaceModel
import no.item.xp.plugin.models.InterfaceModelField
import org.w3c.dom.Node

fun parseFieldSet(fieldSetNode: Node, mixins: List<InterfaceModel>): List<InterfaceModelField> {
  val itemsNode = fieldSetNode.getChildNodeAtXPath("items")
  return itemsNode?.let { parseFields(itemsNode, mixins).getOrNull() } ?: emptyList()
}
