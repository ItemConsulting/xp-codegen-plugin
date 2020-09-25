package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.extensions.getNodeAttribute
import no.item.xp.plugin.models.*
import org.w3c.dom.Node

fun parseUnknownField(inputNode: Node): UnknownField? {
  val comment = inputNode.getChildNodeAtXPath("label")?.textContent
  val name = inputNode.getNodeAttribute("name")
  val minimum = inputNode.getChildNodeAtXPath("occurrences/@minimum")?.nodeValue
  val maximum = inputNode.getChildNodeAtXPath("occurrences/@maximum")?.nodeValue

  val nullable = minimum?.let { Integer.parseInt(it) < 1 } ?: true
  val isArray = maximum?.let { Integer.parseInt(it) != 1 } ?: false

  return name?.let { UnknownField(it, comment, nullable, isArray) }
}
