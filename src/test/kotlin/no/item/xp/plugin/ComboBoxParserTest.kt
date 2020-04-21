package no.item.xp.plugin

import arrow.core.Option
import no.item.xp.plugin.models.GeneratedComboBoxField
import no.item.xp.plugin.models.InputType
import no.item.xp.plugin.parser.parseComboBox
import org.junit.Test
import org.w3c.dom.Node
import kotlin.test.assertEquals

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
    val generatedField: Option<GeneratedComboBoxField> = parseComboBox(node)
    val generated: GeneratedComboBoxField? = generatedField.fold({ null }, { it })
    if (generated != null) {
      assertEquals(generated.name, "invite")
      assertEquals(generated.comment.fold({""},{it}), "Invited")
      assertEquals(generated.nullable, true)
      assertEquals(generated.type, InputType.COMBOBOX)
      generated.optionList.fold(
        { null },
        { it.forEach{
          (key, value) -> println("$key = $value")
        }}
      )
    }
  }
}

