package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseFieldSetTest {
  @Test
  fun `parse FieldSet`() {
    // language=XML
    val xml =
      """
        <field-set name="contactInfo">
          <label>Contact info</label>
          <items>
            <input type="TextLine" name="email">
              <label>Email</label>
              <occurrences minimum="1" maximum="1"/>
            </input>
            <input type="TextLine" name="phoneNumber">
              <label>Phone</label>
              <occurrences minimum="1" maximum="1"/>
            </input>
          </items>
        </field-set>
        """

    val result = parseFieldSet(stringToXMLDocument(xml).getChildNodeAtXPath("field-set")!!, emptyList())

    assertEquals(
      result,
      listOf(
        StringField("email", "Email", false, false),
        StringField("phoneNumber", "Phone", false, false)
      )
    )
  }
}
