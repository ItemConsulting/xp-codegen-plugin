package no.item.xp.plugin

import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Node

fun getNodeFromString(stringNode: String): Node {
  val builder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
  val strBuilder = StringBuilder()
  strBuilder.append(stringNode)
  val byteStream = ByteArrayInputStream(strBuilder.toString().toByteArray())
  val doc: Document = builder.parse(byteStream)
  doc.documentElement.normalize()
  return doc
}
