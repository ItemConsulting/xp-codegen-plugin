package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.BooleanField
import no.item.xp.plugin.models.NumberField
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.models.UnionOfStringLiteralField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.w3c.dom.Node
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseInputTest {
  @Nested
  @DisplayName("TextLine")
  inner class TextLine {
    @Test
    fun `parse TextLine without occurrences`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
          <input type="TextLine" name="firstTextLine">
            <label>The First Text Line</label>
          </input>
          """
        )
      )

      assertEquals(
        result,
        StringField(
          "firstTextLine",
          "The First Text Line",
          true,
          false
        )
      )
    }

    @Test
    fun `parse single nullable TextLine`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
            <input type="TextLine" name="secondTextLine">
              <label>The Second Text Line</label>
              <occurrences minimum="0" maximum="1"/>
            </input>
            """
        )
      )

      assertEquals(
        result,
        StringField(
          "secondTextLine",
          "The Second Text Line",
          true,
          false
        )
      )
    }

    @Test
    fun `parse multiple non-nullable TextLine`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
            <input type="TextLine" name="secondTextLine">
              <label>The Second Text Line</label>
              <occurrences minimum="1" maximum="0"/>
            </input>
            """
        )
      )

      assertEquals(
        result,
        StringField(
          "secondTextLine",
          "The Second Text Line",
          false,
          true
        )
      )
    }

    @Test
    fun `parse single required TextLine`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
            <input type="TextLine" name="thirdTextLine">
              <label>The Third Text Line</label>
              <occurrences minimum="1" maximum="1"/>
            </input>
            """
        )
      )

      assertEquals(result, StringField("thirdTextLine", "The Third Text Line", false, false))
    }

    @Test
    fun `parse TextLine in wrong order`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
              <input type="TextLine" name="forthTextLine">
                <occurrences minimum="1" maximum="1"/>
                <label>The Forth Text Line</label>
                whatever
              </input>
              """
        )
      )

      assertEquals(
        result,
        StringField(
          "forthTextLine",
          "The Forth Text Line",
          false,
          false
        )
      )
    }
  }

  @Nested
  @DisplayName("Checkbox")
  inner class Checkbox {
    @Test
    fun `parse checkbox`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
              <input type="checkbox" name="checkbox">
                <label>checkbox test</label>
              </input>
              """
        )
      )

      assertEquals(
        result,
        BooleanField(
          "checkbox",
          "checkbox test",
          false,
          false
        )
      )
    }
  }

  @Nested
  @DisplayName("ComboBox")
  inner class ComboBox {
    @Test
    fun `parse ComboBox`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
          <input name="invite" type="ComboBox">
            <label>Invited</label>
            <occurrences minimum="0" maximum="1"/>
            <config>
              <option value="Yes">Yes</option>
              <option value="No">No</option>
              <option value="what">Maybe</option>
            </config>
          </input>
          """
        )
      )
      assertEquals(
        result,
        UnionOfStringLiteralField(
          "invite",
          "Invited",
          true,
          false,
          listOf("Yes", "No", "what")
        )
      )
    }
  }

  @Nested
  @DisplayName("Long")
  inner class Long {
    @Test
    fun `parse ComboBox`() {
      val result = parseInput(
        getInputNode(
          // language=XML
          """
          <input name="year" type="Long">
            <label>Year</label>
            <occurrences minimum="0" maximum="1"/>
          </input>
          """
        )
      )
      assertEquals(
        result,
        NumberField(
          "year",
          "Year",
          true,
          false
        )
      )
    }
  }

  private fun getInputNode(xml: String): Node {
    return stringToXMLDocument(xml).getChildNodeAtXPath("input")!!
  }
}
