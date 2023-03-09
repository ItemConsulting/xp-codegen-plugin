package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.extensions.getChildNodesAtXPath
import no.item.xp.plugin.extensions.getNodeAttribute
import no.item.xp.plugin.models.InterfaceModel
import no.item.xp.plugin.models.InterfaceModelField
import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.OptionSetField
import org.w3c.dom.Node

fun parseOptionSet(inputNode: Node, mixins: List<InterfaceModel>): OptionSetField? {
  val unknownField = parseUnknownField(inputNode)

  if (unknownField != null) {
    val optionList = inputNode.getChildNodesAtXPath(xpathOption)
      .map { optionNode -> parseOptionSetFieldOption(optionNode, mixins) }

    val maximum = inputNode.getChildNodeAtXPath("options/@maximum")?.nodeValue
    val isMultiSelect = maximum?.let { Integer.parseInt(it) != 1 } ?: false
    return OptionSetField(unknownField, isMultiSelect, optionList)
  }

  return null
}

private fun parseOptionSetFieldOption(optionNode: Node, mixins: List<InterfaceModel>): ObjectField {
  val optionComment = optionNode.getChildNodeAtXPath("label")?.textContent
  val itemsNode = optionNode.getChildNodeAtXPath("items")

  val fields: List<InterfaceModelField> = itemsNode
    ?.let { parseFields(it, mixins).getOrNull() } ?: emptyList()

  return ObjectField(optionNode.getNodeAttribute("name")!!, optionComment, true, false, fields)
}
