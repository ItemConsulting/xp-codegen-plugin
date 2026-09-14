package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.BooleanField
import no.item.xp.plugin.models.NumberField
import no.item.xp.plugin.models.NumberFieldWithValidation
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.models.StringFieldWithValidation
import no.item.xp.plugin.models.UnionOfStringLiteralField
import no.item.xp.plugin.models.UnknownField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.w3c.dom.Node
import kotlin.test.assertEquals
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseInputTest {
  @Nested
  @DisplayName("TextLine")
  inner class TextLine {
    @Test
    fun `parse TextLine without occurrences`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input type="TextLine" name="firstTextLine">
            <label>The First Text Line</label>
          </input>
          """,
          ),
        )

      assertEquals(
        StringField(
          "firstTextLine",
          "The First Text Line",
          true,
          false,
        ),
        result,
      )
    }

    @Test
    fun `parse single nullable TextLine`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
            <input type="TextLine" name="secondTextLine">
              <label>The Second Text Line</label>
              <occurrences minimum="0" maximum="1"/>
            </input>
            """,
          ),
        )

      assertEquals(
        StringField(
          "secondTextLine",
          "The Second Text Line",
          true,
          false,
        ),
        result,
      )
    }

    @Test
    fun `parse multiple non-nullable TextLine`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
            <input type="TextLine" name="secondTextLine">
              <label>The Second Text Line</label>
              <occurrences minimum="1" maximum="0"/>
            </input>
            """,
          ),
        )

      assertEquals(
        StringField(
          "secondTextLine",
          "The Second Text Line",
          false,
          true,
        ),
        result,
      )
    }

    @Test
    fun `parse single required TextLine`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
            <input type="TextLine" name="thirdTextLine">
              <label>The Third Text Line</label>
              <occurrences minimum="1" maximum="1"/>
            </input>
            """,
          ),
        )

      assertEquals(StringField("thirdTextLine", "The Third Text Line", false, false), result)
    }

    @Test
    fun `parse TextLine in wrong order`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
              <input type="TextLine" name="forthTextLine">
                <occurrences minimum="1" maximum="1"/>
                <label>The Forth Text Line</label>
                whatever
              </input>
              """,
          ),
        )

      assertEquals(
        StringField(
          "forthTextLine",
          "The Forth Text Line",
          false,
          false,
        ),
        result,
      )
    }

    @Test
    fun `parse TextLine with regexp`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
            <input type="TextLine" name="postalCode">
              <label>Postal code</label>
              <config>
                <regexp>^\d{4}$</regexp>
              </config>
            </input>
            """,
          ),
        )

      assertEquals(StringFieldWithValidation("postalCode", "Postal code", true, false, "^\\d{4}$", null), result)
    }

    @Test
    fun `parse TextArea with max-length`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
            <input type="TextArea" name="intro">
              <label>Intro</label>
              <config>
                <max-length>100</max-length>
              </config>
            </input>
            """,
          ),
        )

      assertEquals(StringFieldWithValidation("intro", "Intro", true, false, null, 100), result)
    }
  }

  @Nested
  @DisplayName("Strings")
  inner class Strings {
    @ParameterizedTest
    @ValueSource(
      strings = [
        "TextArea", "HtmlArea", "GeoPoint", "ContentSelector", "ImageSelector", "MediaSelector", "AttachmentUploader",
        "CustomSelector", "Tag", "ContentTypeFilter",
      ],
    )
    fun `parse input types as strings`(type: String) {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
            <input type="$type" name="value">
              <label>Value</label>
              <occurrences minimum="0" maximum="0"/>
            </input>
            """,
          ),
        )

      assertEquals(StringField("value", "Value", true, true), result)
    }

    @Test
    fun `parse Date, Time and DateTime with regexp`() {
      mapOf("Date" to REGEX_DATE, "Time" to REGEX_TIME, "DateTime" to REGEX_DATETIME).forEach { (type, regexp) ->
        val result =
          parseInput(
            getInputNode(
              // language=XML
              """
              <input type="$type" name="value">
                <label>Value</label>
                <occurrences minimum="1" maximum="1"/>
              </input>
              """,
            ),
          )

        assertEquals(StringFieldWithValidation("value", "Value", false, false, regexp, null), result, type)
      }
    }
  }

  @Nested
  @DisplayName("Checkbox")
  inner class Checkbox {
    @Test
    fun `parse checkbox`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
              <input type="checkbox" name="checkbox">
                <label>checkbox test</label>
              </input>
              """,
          ),
        )

      assertEquals(
        BooleanField(
          "checkbox",
          "checkbox test",
          false,
          false,
        ),
        result,
      )
    }

    @Test
    fun `parse CheckBox as required and single, regardless of occurrences`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
              <input type="CheckBox" name="checkbox">
                <label>Check box</label>
                <occurrences minimum="0" maximum="0"/>
              </input>
              """,
          ),
        )

      assertEquals(BooleanField("checkbox", "Check box", false, false), result)
    }
  }

  @Nested
  @DisplayName("ComboBox")
  inner class ComboBox {
    @Test
    fun `parse ComboBox`() {
      val result =
        parseInput(
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
          """,
          ),
        )

      assertEquals(
        UnionOfStringLiteralField(
          "invite",
          "Invited",
          true,
          false,
          listOf("Yes", "No", "what"),
        ),
        result,
      )
    }
  }

  @Nested
  @DisplayName("RadioButton")
  inner class RadioButton {
    @Test
    fun `parse RadioButton as single, regardless of occurrences`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input name="answer" type="RadioButton">
            <label>Answer</label>
            <occurrences minimum="1" maximum="0"/>
            <config>
              <option value="yes">Yes</option>
              <option value="no">No</option>
            </config>
          </input>
          """,
          ),
        )

      assertEquals(UnionOfStringLiteralField("answer", "Answer", false, false, listOf("yes", "no")), result)
    }
  }

  @Nested
  @DisplayName("Numbers")
  inner class Numbers {
    @Test
    fun `parse Long`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input name="year" type="Long">
            <label>Year</label>
            <occurrences minimum="0" maximum="1"/>
          </input>
          """,
          ),
        )

      assertEquals(
        NumberField(
          "year",
          "Year",
          true,
          false,
        ),
        result,
      )
    }

    @Test
    fun `parse Double with min and max`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input name="rating" type="Double">
            <label>Rating</label>
            <occurrences minimum="1" maximum="0"/>
            <config>
              <min>1</min>
              <max>5</max>
            </config>
          </input>
          """,
          ),
        )

      assertEquals(NumberFieldWithValidation("rating", "Rating", false, true, 1, 5), result)
    }
  }

  @Nested
  @DisplayName("Other")
  inner class Other {
    @Test
    fun `parse unknown input type`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input name="custom" type="MyCustomInput">
            <label>Custom</label>
          </input>
          """,
          ),
        )

      assertEquals(UnknownField("custom", "Custom", true, false), result)
    }

    @Test
    fun `parse input without label`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input name="title" type="TextLine"/>
          """,
          ),
        )

      assertEquals(StringField("title", null, true, false), result)
    }

    @Test
    fun `ignore input without name`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input type="TextLine">
            <label>Title</label>
          </input>
          """,
          ),
        )

      assertNull(result)
    }

    @Test
    fun `ignore input without type`() {
      val result =
        parseInput(
          getInputNode(
            // language=XML
            """
          <input name="title">
            <label>Title</label>
          </input>
          """,
          ),
        )

      assertNull(result)
    }
  }

  private fun getInputNode(xml: String): Node = stringToXMLDocument(xml).getChildNodeAtXPath("input")!!
}
