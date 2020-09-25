package no.item.xp.plugin

import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

val DOCUMENT_BUILDER_FACTORY: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

fun stringToXMLDocument(str: String): Document {
  val builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder()
  val doc = builder.parse(ByteArrayInputStream(str.toByteArray()))
  doc.documentElement.normalize()
  return doc
}
