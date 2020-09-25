package no.item.xp.plugin.extensions

import arrow.core.Either
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import no.item.xp.plugin.NoFormException
import no.item.xp.plugin.parser.XPATH_FACTORY
import no.item.xp.plugin.util.getXpathExpressionFromString
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression

val xpathFormElement: XPathExpression = XPATH_FACTORY.newXPath().compile("//form")

fun Node.getChildNodeAtXPath(xpath: XPathExpression): Node? =
  xpath.evaluate(this, XPathConstants.NODE) as Node?

fun Node.getChildNodeAtXPath(xpath: String): Node? =
  getChildNodeAtXPath(getXpathExpressionFromString(xpath))

fun Node.getChildNodesAtXPath(xpath: XPathExpression): Collection<Node> =
  (xpath.evaluate(this, XPathConstants.NODESET) as NodeList).toCollection()

fun Node.getFormNode(): Either<Throwable, Node> =
  try {
    (xpathFormElement.evaluate(this, XPathConstants.NODE) as Node?)?.right()
      ?: NoFormException("No form node").left()
  } catch (e: Exception) {
    e.nonFatalOrThrow().left()
  }

fun Node.getChildNodesAtXPathAsEither(xpath: XPathExpression): Either<Throwable, Collection<Node>> =
  try {
    (xpath.evaluate(this, XPathConstants.NODESET) as NodeList).toCollection().right()
  } catch (e: Exception) {
    e.nonFatalOrThrow().left()
  }

fun Node.getNodeAttribute(name: String): String? {
  return this.attributes?.getNamedItem(name)?.nodeValue
}
