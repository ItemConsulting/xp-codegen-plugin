package no.item.xp.plugin.util

import arrow.core.*
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

val XPATH_FACTORY: XPathFactory = XPathFactory.newInstance()
val XPATH_FORM_ELEMENT: XPathExpression = XPATH_FACTORY.newXPath().compile("//form/*[type]")
val XPATH_MINIMUM_ELEMENT: XPathExpression = XPATH_FACTORY.newXPath().compile("//input/occurrences/@minimum")

fun getFormElementChildren(doc: Document) = getNodesByXpath(doc, XPATH_FORM_ELEMENT)

fun getMinimumOccurrences(node: Node) = getNodeByXpathAndNode(node, XPATH_MINIMUM_ELEMENT)

fun getNodesByXpath(doc: Document, xpath: XPathExpression): Either<Throwable, Sequence<Node>> =
  try {
    Either.right(nodeListToSequence(xpath.evaluate(doc, XPathConstants.NODESET) as NodeList))
  } catch (e: Exception) {
    Either.left(e)
  }

fun getNodeByXpathAndNode(node: Node, xpath: XPathExpression): Option<Node> {
  return if (xpath.evaluate(node, XPathConstants.NODE) == null) {
    none()
  } else {
    Some(xpath.evaluate(node, XPathConstants.NODE) as Node)
  }
}

fun isOptional(node: Node): Option<Boolean> =
  getMinimumOccurrences(node)
    .fold(
      { none() },
      { Some(isNotZero(it)) }
    )

fun isNotZero(node: Node): Boolean = Integer.parseInt(node.nodeValue) < 1
