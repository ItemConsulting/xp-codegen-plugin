package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.models.OptionSetField
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseSetWithMixinTest {
  private val urlField = StringField("url", "Url", false, false)
  private val mixins = listOf(ObjectTypeModel("link", listOf(urlField)))

  @Test
  fun `parse ItemSet with only a mixin`() {
    // language=XML
    val xml =
      """
        <item-set name="links">
          <label>Links</label>
          <occurrences minimum="0" maximum="0"/>
          <items>
            <mixin name="link"/>
          </items>
        </item-set>
        """

    val result = parseItemSet(stringToXMLDocument(xml).getChildNodeAtXPath("item-set")!!, mixins)

    assertEquals(
      ObjectField("links", "Links", true, true, listOf(urlField), "link"),
      result,
    )
  }

  @Test
  fun `parse ItemSet with a mixin and other fields`() {
    // language=XML
    val xml =
      """
        <item-set name="links">
          <label>Links</label>
          <occurrences minimum="0" maximum="0"/>
          <items>
            <input name="title" type="TextLine">
              <label>Title</label>
              <occurrences minimum="0" maximum="1"/>
            </input>
            <mixin name="link"/>
          </items>
        </item-set>
        """

    val result = parseItemSet(stringToXMLDocument(xml).getChildNodeAtXPath("item-set")!!, mixins)

    assertEquals(
      ObjectField("links", "Links", true, true, listOf(StringField("title", "Title", true, false), urlField), null),
      result,
    )
  }

  @Test
  fun `parse ItemSet with only an unknown mixin`() {
    // language=XML
    val xml =
      """
        <item-set name="links">
          <label>Links</label>
          <occurrences minimum="0" maximum="0"/>
          <items>
            <mixin name="missing"/>
          </items>
        </item-set>
        """

    val result = parseItemSet(stringToXMLDocument(xml).getChildNodeAtXPath("item-set")!!, mixins)

    assertEquals(
      ObjectField("links", "Links", true, true, emptyList(), null),
      result,
    )
  }

  @Test
  fun `parse OptionSet option with only a mixin`() {
    // language=XML
    val xml =
      """
        <option-set name="myLink">
          <label>Link</label>
          <occurrences minimum="1" maximum="1"/>
          <options minimum="1" maximum="1">
            <option name="none">
              <label>None</label>
            </option>
            <option name="internal">
              <label>Internal</label>
              <items>
                <mixin name="link"/>
              </items>
            </option>
          </options>
        </option-set>
        """

    val result = parseOptionSet(stringToXMLDocument(xml).getChildNodeAtXPath("option-set")!!, mixins)

    assertEquals(
      OptionSetField(
        "myLink",
        "Link",
        false,
        false,
        false,
        listOf(
          ObjectField("none", "None", true, false, emptyList(), null),
          ObjectField("internal", "Internal", true, false, listOf(urlField), "link"),
        ),
      ),
      result,
    )
  }
}
