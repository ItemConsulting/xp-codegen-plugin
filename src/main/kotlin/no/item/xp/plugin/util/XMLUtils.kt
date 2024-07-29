package no.item.xp.plugin.util

import arrow.core.Either
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import org.w3c.dom.Document
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

val XPATH_FACTORY: XPathFactory = XPathFactory.newInstance()

val documentBuilderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

fun parseXml(file: InputStream): Either<Throwable, Document> =
  try {
    val builder = documentBuilderFactory.newDocumentBuilder()
    builder.setErrorHandler(null)
    builder.parse(file).right()
  } catch (t: Throwable) {
    t.nonFatalOrThrow().left()
  }

fun getXpathExpressionFromString(xpath: String): XPathExpression {
  return XPATH_FACTORY.newXPath().compile(xpath)
}
