package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.InterfaceModel
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseMixinTest {
  @Test
  fun `resolve Mixin graph`() {
    // language=XML
    val xml1 =
      """
      <mixin>
        <display-name>AA</display-name>
      
        <form>
          <input name="isA" type="TextLine">
            <label>Is A</label>
            <occurrences minimum="0" maximum="1"/>
          </input>
          <mixin name="cc" />
        </form>
      </mixin>
      """

    // language=XML
    val xml2 =
      """
      <mixin>
        <display-name>BB</display-name>
      
        <form>
          <input name="isB" type="TextLine">
            <label>Is B</label>
            <occurrences minimum="0" maximum="1"/>
          </input>
        </form>
      </mixin>
      """

    // language=XML
    val xml3 =
      """
      <mixin>
        <display-name>CC</display-name>
      
        <form>
          <input name="isC" type="TextLine">
            <label>Is C</label>
            <occurrences minimum="0" maximum="1"/>
          </input>
      
          <mixin name="bb" />
        </form>
      </mixin>
      """

    val mixinDependencies = mapOf("aa" to xml1, "bb" to xml2, "cc" to xml3)
      .map { (name, xml) ->
        val doc = stringToXMLDocument(xml)
        val node = doc.getChildNodeAtXPath("mixin/form")!!
        parseMixinDependencyModel(node, name)
      }

    val result = mixinDependencies
      .mapNotNull { parseMixin(it, mixinDependencies) }

    assertEquals(
      result,
      listOf(
        InterfaceModel(
          "aa",
          listOf(
            StringField("isA", "Is A", true, false),
            StringField("isC", "Is C", true, false),
            StringField("isB", "Is B", true, false)
          )
        ),
        InterfaceModel(
          "bb",
          listOf(
            StringField("isB", "Is B", true, false)
          )
        ),
        InterfaceModel(
          "cc",
          listOf(
            StringField("isC", "Is C", true, false),
            StringField("isB", "Is B", true, false)
          )
        )
      )
    )
  }
}
