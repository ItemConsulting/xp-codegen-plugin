package no.item.xp.plugin

import arrow.core.Option
import arrow.core.extensions.sequence.foldable.isEmpty
import no.item.xp.plugin.models.GeneratedInputType
import no.item.xp.plugin.models.MultipleField
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.parser.parseContentSelector
import org.junit.Test
import org.w3c.dom.Node
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ContentSelectorParserTest {

  @Test
  fun contentSelectorSingle() {
    val nodeString: String =
      "<input name=\"fabTarget\" type=\"ContentSelector\">" +
        "<label>FAB button target</label>" +
        "<occurrences minimum=\"0\" maximum=\"1\"/>" +
        "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<GeneratedInputType> = parseContentSelector(node)
    val generated: GeneratedInputType? = generatedField.fold({ null }, { it })
    if (generated != null) {
      when (generated) {
        is StringField -> runEqualsSingle(generated)
        else -> assertFalse { true }
      }
    }
  }

  @Test
  fun contentSelectorMultiple() {
    val nodeString: String =
      "<input name=\"members\" type=\"ContentSelector\">" +
        "<label>Members</label>" +
        "<occurrences minimum=\"0\" maximum=\"2\"/>" +
        "<config>" +
        "<allowContentType>employee</allowContentType>" +
        "</config>" +
        "</input>"
    val node: Node = getNodeFromString(nodeString)
    val generatedField: Option<GeneratedInputType> = parseContentSelector(node)
    val generated: GeneratedInputType? = generatedField.fold({ null }, { it })
    if (generated != null) {
      when (generated) {
        is MultipleField -> runEqualsMultiple(generated)
        else -> assertFalse { true }
      }
    }
  }

  private fun runEqualsSingle(generated: StringField) {
    assertEquals(generated.name, "fabTarget")
    assertEquals(generated.comment.fold({ "" }, { it }), "FAB button target")
    assertEquals(generated.nullable, true)
  }
  private fun runEqualsMultiple(generated: MultipleField) {
    assertEquals(generated.name, "members")
    assertEquals(generated.comment.fold({ "" }, { it }), "Members")
    assertEquals(generated.nullable, true)
    assertFalse(generated.configList.isEmpty())
  }
}
