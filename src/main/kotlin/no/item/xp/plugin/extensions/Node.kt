@file:Suppress("unused")

package no.item.xp.plugin.extensions

import arrow.core.Option
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import no.item.xp.plugin.util.getXpathExpressionFromString
import org.w3c.dom.Node

fun Node.getTextContentAsOption(): Option<String> =
  Option.fromNullable(this.textContent)

fun Node.getChildNodeAtXPath(xpath: XPathExpression): Option<Node> =
  Option.fromNullable(xpath.evaluate(this, XPathConstants.NODE) as Node?)

fun Node.getChildNodeAtXPath(xpath: String): Option<Node> =
  getChildNodeAtXPath(getXpathExpressionFromString(xpath))

fun Node.getAttributeAsOption(attributeName: String): Option<String> =
  Option.fromNullable(this.attributes)
    .flatMap {
      Option.fromNullable(it.getNamedItem(attributeName))
    }
    .map {
      it.nodeValue
    }

fun Node.getChildNodesSequence(): Sequence<Node> =
  (0 until this.childNodes.length)
      .asSequence()
      .map { this.childNodes.item(it) }
