package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.models.ObjectTypeModelField
import org.w3c.dom.Node

fun parseFieldSet(fieldSetNode: Node, mixins: List<ObjectTypeModel>): List<ObjectTypeModelField> {
  val itemsNode = fieldSetNode.getChildNodeAtXPath("items")
  return itemsNode?.let { parseFields(itemsNode, mixins).getOrNull() } ?: emptyList()
}
