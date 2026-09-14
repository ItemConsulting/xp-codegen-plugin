package no.item.xp.plugin.parser

import no.item.xp.plugin.DuplicateFieldNameException
import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseDuplicateFieldNameTest {
  private val mixins = listOf(ObjectTypeModel("intro", listOf(StringField("intro", "Intro", true, false))))

  private fun parseForm(xml: String) =
    parseObjectTypeModel(stringToXMLDocument(xml).getChildNodeAtXPath("content-type/form")!!, "article", mixins)

  @Test
  fun `fail if mixin contains field with same name`() {
    // language=XML
    val xml =
      """
      <content-type>
        <form>
          <input name="intro" type="TextLine">
            <label>Intro</label>
          </input>
          <mixin name="intro"/>
        </form>
      </content-type>
      """

    val exception = assertFailsWith<DuplicateFieldNameException> { parseForm(xml) }

    assertEquals(listOf("intro"), exception.fieldNames)
  }

  @Test
  fun `fail if field-set contains field with same name`() {
    // language=XML
    val xml =
      """
      <content-type>
        <form>
          <item-set name="block">
            <items>
              <input name="title" type="TextLine"/>
              <field-set>
                <items>
                  <input name="title" type="TextArea"/>
                </items>
              </field-set>
            </items>
          </item-set>
        </form>
      </content-type>
      """

    val exception = assertFailsWith<DuplicateFieldNameException> { parseForm(xml) }

    assertEquals(listOf("title"), exception.fieldNames)
  }

  @Test
  fun `allow same field name in different objects`() {
    // language=XML
    val xml =
      """
      <content-type>
        <form>
          <input name="title" type="TextLine"/>
          <item-set name="block">
            <items>
              <input name="title" type="TextLine"/>
            </items>
          </item-set>
          <option-set name="link">
            <options minimum="1" maximum="1">
              <option name="internal">
                <items>
                  <input name="title" type="TextLine"/>
                </items>
              </option>
            </options>
          </option-set>
        </form>
      </content-type>
      """

    assertEquals(3, parseForm(xml).getOrNull()?.fields?.size)
  }

  @Test
  fun `fail with name of mixin that contains duplicates`() {
    // language=XML
    val xml =
      """
      <mixin>
        <form>
          <input name="intro" type="TextLine"/>
          <mixin name="bb"/>
        </form>
      </mixin>
      """

    // language=XML
    val xml2 =
      """
      <mixin>
        <form>
          <input name="intro" type="TextArea"/>
        </form>
      </mixin>
      """

    val mixinDependencies =
      mapOf("aa" to xml, "bb" to xml2)
        .map { (name, xml) -> parseMixinDependencyModel(stringToXMLDocument(xml).getChildNodeAtXPath("mixin/form")!!, name) }

    val exception = assertFailsWith<DuplicateFieldNameException> { parseMixin(mixinDependencies.first(), mixinDependencies) }

    assertEquals(
      "Duplicate field name \"intro\" in \"site/mixins/aa/aa.xml\". A field name can only be used once in the same object.",
      exception.message,
    )
  }
}
