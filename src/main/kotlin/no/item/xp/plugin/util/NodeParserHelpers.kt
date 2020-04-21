package no.item.xp.plugin.util

import arrow.core.Option
import no.item.xp.plugin.extensions.*
import org.w3c.dom.Node

fun getCommentForGeneratedField(node: Node): Option<String> =
  node.getChildNodeAtXPath(getXpathExpressionFromString("//label"))
    .flatMap(Node::getTextContentAsOption)

fun getNameForGeneratedField(node: Node): Option<String> =
  node.getAttributeAsOption("name")

fun getChildNodesTextContent(node: Node): Sequence<String> {
  return node
    .getChildNodesSequence()
    .map { it.textContent.toString() }
}
