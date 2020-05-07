package no.item.xp.plugin

import arrow.core.Option
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import no.item.xp.plugin.models.UnionOfStringField
import no.item.xp.plugin.parser.parseComboBox
import org.junit.Test
import org.w3c.dom.Node

class ComboBoxParserTest {

  @Test
  fun textLineParserTestNoOccurrences() {
    val nodeString: String =
        "<input name=\"invite\" type=\"ComboBox\">" +
          "<label>Invited</label>" +
          "<occurrences minimum=\"0\" maximum=\"1\"/>" +
          "<config>" +
            "<option value=\"Yes\">Yes</option>" +
            "<option value=\"No\">No</option>" +
            "<option value=\"what\">Maybe</option>" +
          "</config>" +
        "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<UnionOfStringField> = parseComboBox(node)
    val generated: UnionOfStringField? = generatedField.fold({ null }, { it })
    val exists: Array<String> = arrayOf("Yes", "No", "Maybe")
    if (generated != null) {
      assertEquals(generated.name, "invite")
      assertEquals(generated.comment.fold({ "" }, { it }), "Invited")
      assertEquals(generated.nullable, true)
      generated.optionList.forEach {
        it in exists
      }
    } else {
        assertFalse { true }
    }
  }
}
