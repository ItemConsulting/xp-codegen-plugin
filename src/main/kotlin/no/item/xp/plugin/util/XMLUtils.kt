package no.item.xp.plugin.util

import arrow.core.*
import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.YesNoMaybe
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

val XPATH_FACTORY: XPathFactory = XPathFactory.newInstance()
val XPATH_FORM_ELEMENT: XPathExpression = XPATH_FACTORY.newXPath().compile("//form/*[type]")
val XPATH_MINIMUM_ELEMENT: XPathExpression = XPATH_FACTORY.newXPath().compile("//input/occurrences/@minimum")
val XPATH_CONFIG: XPathExpression = XPATH_FACTORY.newXPath().compile("//config")

fun getFormElementChildren(doc: Document): Either<Throwable, Sequence<Node>> =
  getNodesByXpath(doc, XPATH_FORM_ELEMENT)

fun  getXpathExpressionFromString(xpath: String): XPathExpression =
  XPATH_FACTORY.newXPath().compile(xpath)

fun getNodesByXpath(doc: Document, xpath: XPathExpression): Either<Throwable, Sequence<Node>> =
  try {
    Either.right(nodeListToSequence(xpath.evaluate(doc, XPathConstants.NODESET) as NodeList))
  } catch (e: Exception) {
    Either.left(e)
  }

fun isOptional(node: Node): Boolean =
  node.getChildNodeAtXPath(XPATH_MINIMUM_ELEMENT)
    .fold(
      { true },
      { isNotZero(it) }
    )

fun isNotZero(node: Node): Boolean = Integer.parseInt(node.nodeValue) < 1

fun getConfigOptions(node:Node): Option<HashMap<YesNoMaybe, Boolean>> =
  node.getChildNodeAtXPath(XPATH_CONFIG)
  .flatMap {
    mapToHashMap(it)
  }