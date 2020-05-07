package no.item.xp.plugin

import arrow.core.Option
import kotlin.test.Test
import kotlin.test.assertEquals
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.parser.parseTextLine
import org.w3c.dom.Node

class TextLineParserTest {

  @Test
  fun textLineParserTestNoOccurrences() {
    val nodeString: String =
      "<input type=\"TextLine\" name=\"firstTextLine\">" +
      "<label>The First Text Line</label>" +
      "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<StringField> = parseTextLine(node)
    val generated: StringField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, true)
      assertEquals(generated.comment.fold({ "" }, { it }), "The First Text Line")
      assertEquals(generated.name, "firstTextLine")
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
    val generatedField: Option<StringField> = parseTextLine(node)
    val generated: StringField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, true)
      assertEquals(generated.comment.fold({ "" }, { it }), "The Second Text Line")
      assertEquals(generated.name, "secondTextLine")
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
    val generatedField: Option<StringField> = parseTextLine(node)
    val generated: StringField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, false)
      assertEquals(generated.comment.fold({ "" }, { it }), "The Third Text Line")
      assertEquals(generated.name, "thirdTextLine")
    }
  }
  @Test
  fun textLineParserTestWrongOrder() {
    val nodeString: String =
      "<input type=\"TextLine\" name=\"thirdTextLine\">" +
        "<occurrences minimum=\"1\" maximum=\"1\"/>" +
        "<label>The Third Text Line</label>" +
        "whatever" +
        "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<StringField> = parseTextLine(node)
    val generated: StringField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, false)
      assertEquals(generated.comment.fold({ "" }, { it }), "The Third Text Line")
      assertEquals(generated.name, "thirdTextLine")
    }
  }
}
