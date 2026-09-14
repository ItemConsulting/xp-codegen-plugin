package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseItemSetTest {
  @Test
  fun `parse ItemSet`() {
    // language=XML
    val xml =
      """
        <item-set name="contactInfo">
          <label>Contact Info</label>
          <occurrences minimum="0" maximum="0"/>
          <items>
            <input name="label" type="TextLine">
              <label>Label</label>
              <occurrences minimum="0" maximum="1"/>
            </input>
            <input name="phoneNumber" type="TextLine">
              <label>Phone Number</label>
              <occurrences minimum="0" maximum="1"/>
            </input>
          </items>
        </item-set>
        """

    val result = parseItemSet(stringToXMLDocument(xml).getChildNodeAtXPath("item-set")!!, emptyList())

    assertEquals(
      ObjectField(
        "contactInfo",
        "Contact Info",
        true,
        true,
        listOf(
          StringField("label", "Label", true, false),
          StringField("phoneNumber", "Phone Number", true, false),
        ),
      ),
      result,
    )
  }

  @Test
  fun `parse nested ItemSet`() {
    // language=XML
    val xml =
      """
        <item-set name="contact">
          <label>Contact</label>
          <occurrences minimum="1" maximum="1"/>
          <items>
            <item-set name="phoneNumbers">
              <label>Phone numbers</label>
              <occurrences minimum="0" maximum="0"/>
              <items>
                <input name="number" type="TextLine">
                  <label>Number</label>
                  <occurrences minimum="1" maximum="1"/>
                </input>
              </items>
            </item-set>
          </items>
        </item-set>
        """

    val result = parseItemSet(stringToXMLDocument(xml).getChildNodeAtXPath("item-set")!!, emptyList())

    assertEquals(
      ObjectField(
        "contact",
        "Contact",
        false,
        false,
        listOf(
          ObjectField(
            "phoneNumbers",
            "Phone numbers",
            true,
            true,
            listOf(StringField("number", "Number", false, false)),
          ),
        ),
      ),
      result,
    )
  }
}
