package no.item.xp.plugin.phrases

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.StringReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetAllI18nKeyValuesTest {
  private val reader: StringReader =
    StringReader(
      // language=xml
      """
      <part xmlns="urn:enonic:xp:model:1.0">
        <display-name i18n="articleHeader.displayName">Article header</display-name>
        <description i18n="articleHeader.description">Detailed view of an articles</description>

        <form>
          <input name="showParentLink" type="Checkbox">
            <label i18n="articleHeader.showParentLink">Show parent link</label>
            <default>checked</default>
          </input>

          <input name="showPublished" type="Checkbox">
            <label i18n="articleHeader.showPublished">Show published date</label>
            <default>checked</default>
          </input>
        </form>
      </part>
      """,
    )

  @Throws(XMLStreamException::class)
  @Test
  fun `parse xml for i18n keys and values`() {
    // Arrange
    val inputFactory = XMLInputFactory.newInstance()
    val xmlReader = inputFactory.createXMLStreamReader(reader)

    val result = getAllI18nKeyValues(xmlReader)

    val expected =
      mapOf(
        "articleHeader.showParentLink" to "Show parent link",
        "articleHeader.description" to "Detailed view of an articles",
        "articleHeader.showPublished" to "Show published date",
        "articleHeader.displayName" to "Article header",
      )

    assertEquals(expected, result)
  }
}
