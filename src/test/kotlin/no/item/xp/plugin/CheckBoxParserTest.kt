package no.item.xp.plugin

import arrow.core.Option
import no.item.xp.plugin.models.BooleanField
import no.item.xp.plugin.parser.parseCheckBox
import org.junit.Test
import org.w3c.dom.Node
import kotlin.test.assertEquals

class CheckBoxParserTest {
  @Test
  fun textLineParserTestNoOccurrences() {
    val nodeString: String =
      "<input type=\"checkbox\" name=\"checkbox\">" +
        "<label>checkbox test</label>" +
        "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<BooleanField> = parseCheckBox(node)
    val generated: BooleanField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.nullable, false)
      assertEquals(generated.comment.fold({ "" }, { it }), "checkbox test")
      assertEquals(generated.name, "checkbox")
    }
  }
}