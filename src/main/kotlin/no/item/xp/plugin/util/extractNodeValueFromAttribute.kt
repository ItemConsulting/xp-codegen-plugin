package no.item.xp.plugin.util

import arrow.core.Option
import arrow.core.Some
import arrow.core.none
import org.w3c.dom.Node

fun extractNodeValueFromAttribute(node: Node, inputNodeName: String): Option<String> {
  var inputType: Option<String> = none()
  if (hasAttributes(node)) {
    node.attributes
      .forEach {
        if (isNodeNameEquals(it, inputNodeName)) {
          inputType = Some(it.nodeValue)
        }
      }
  }
  return inputType
}

fun hasAttributes(node: Node): Boolean = node.hasAttributes() && node.attributes != null

fun isNodeNameEquals(node: Node, inputNodeName: String): Boolean = node.nodeName == inputNodeName
