package no.item.xp.plugin

import arrow.core.Option
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import no.item.xp.plugin.models.GeneratedField
import no.item.xp.plugin.models.InputType
import no.item.xp.plugin.parser.parseTextLine
import org.w3c.dom.Document
import org.w3c.dom.Node

class TextLineParserTest {

  private fun getNodeFromString(stringNode: String): Node {
    val builder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val strBuilder = StringBuilder()
    strBuilder.append(stringNode)
    val byteStream = ByteArrayInputStream(strBuilder.toString().toByteArray())
    val doc: Document = builder.parse(byteStream)
    doc.documentElement.normalize()
    return doc
  }

  @Test
  fun textLineParserTestNoOccurrences() {
    val nodeString: String =
      "<input type=\"COMBOBOX\" name=\"firstTextLine\">" +
      "<label>The First Text Line</label>" +
      "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<GeneratedField> = parseTextLine(node)
    val generated: GeneratedField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, true)
      assertEquals(generated.comment, "The First Text Line")
      assertEquals(generated.name, "firstTextLine")
      assertEquals(generated.type, InputType.COMBOBOX)
    }
  }

  @Test
  fun textLineParserTestTest() {
    val nodeString: String =
      "<input type=\"TextLine\" name=\"secondTextLine\">" +
      "<label>The Second Text Line</label>" +
      "<occurrences minimum=\"0\" maximum=\"1\"/>" +
      "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<GeneratedField> = parseTextLine(node)
    val generated: GeneratedField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, true)
      assertEquals(generated.comment, "The Second Text Line")
      assertEquals(generated.name, "secondTextLine")
      assertEquals(generated.type, InputType.TEXTLINE)
    }
  }

  @Test
  fun textLineParserTestOccurrences() {
    val nodeString: String =
        "<input type=\"TextLine\" name=\"thirdTextLine\">" +
        "<label>The Third Text Line</label>" +
        "<occurrences minimum=\"1\" maximum=\"1\"/>" +
        "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<GeneratedField> = parseTextLine(node)
    val generated: GeneratedField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, false)
      assertEquals(generated.comment, "The Third Text Line")
      assertEquals(generated.name, "thirdTextLine")
      assertEquals(generated.type, InputType.TEXTLINE)
    }
  }
  @Test
  fun textLineParserTestWrongOrder() {
    val nodeString: String =
      "<input type=\"TextLine\" name=\"thirdTextLine\">" +
        "<occurrences minimum=\"1\" maximum=\"1\"/>" +
        "<label>The Third Text Line</label>" +
        "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<GeneratedField> = parseTextLine(node)
    val generated: GeneratedField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, false)
      assertEquals(generated.comment, "The Third Text Line")
      assertEquals(generated.name, "thirdTextLine")
      assertEquals(generated.type, InputType.TEXTLINE)
    }
  }
}
