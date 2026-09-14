package no.item.xp.codegen.parse

import no.item.xp.codegen.input
import no.item.xp.codegen.model.BooleanField
import no.item.xp.codegen.model.NumberField
import no.item.xp.codegen.model.NumberFieldWithValidation
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.StringFieldWithValidation
import no.item.xp.codegen.model.UnionOfStringLiteralField
import no.item.xp.codegen.model.UnknownField
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParseInputTest {
  @Nested
  @DisplayName("TextLine")
  inner class TextLine {
    @Test
    fun `parse TextLine without occurrences`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            name: "firstTextLine"
            label: "The First Text Line"
            """,
          ),
        )

      assertEquals(StringField("firstTextLine", "The First Text Line", true, false), result)
    }

    @Test
    fun `parse single nullable TextLine`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            name: "secondTextLine"
            label: "The Second Text Line"
            occurrences:
              min: 0
              max: 1
            """,
          ),
        )

      assertEquals(StringField("secondTextLine", "The Second Text Line", true, false), result)
    }

    @Test
    fun `parse multiple non-nullable TextLine`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            name: "secondTextLine"
            label: "The Second Text Line"
            occurrences:
              min: 1
              max: 0
            """,
          ),
        )

      assertEquals(StringField("secondTextLine", "The Second Text Line", false, true), result)
    }

    @Test
    fun `parse single required TextLine`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            name: "thirdTextLine"
            label: "The Third Text Line"
            occurrences:
              min: 1
              max: 1
            """,
          ),
        )

      assertEquals(StringField("thirdTextLine", "The Third Text Line", false, false), result)
    }

    @Test
    fun `parse TextLine with properties in different order`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            occurrences:
              max: 1
              min: 1
            label: "The Forth Text Line"
            name: "forthTextLine"
            type: "TextLine"
            """,
          ),
        )

      assertEquals(StringField("forthTextLine", "The Forth Text Line", false, false), result)
    }

    @Test
    fun `parse TextLine with regexp`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            name: "postalCode"
            label: "Postal code"
            regexp: '^\d{4}$'
            """,
          ),
        )

      assertEquals(StringFieldWithValidation("postalCode", "Postal code", true, false, "^\\d{4}$", null), result)
    }

    @Test
    fun `parse TextArea with maxLength`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextArea"
            name: "intro"
            label: "Intro"
            maxLength: 100
            """,
          ),
        )

      assertEquals(StringFieldWithValidation("intro", "Intro", true, false, null, 100), result)
    }

    @Test
    fun `parse TextLine with localized label`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            name: "title"
            label:
              text: "Title"
              i18n: "article.title"
            """,
          ),
        )

      assertEquals(StringField("title", "Title", true, false), result)
    }
  }

  @Nested
  @DisplayName("Strings")
  inner class Strings {
    @ParameterizedTest
    @ValueSource(
      strings = [
        "TextArea", "HtmlArea", "GeoPoint", "ContentSelector", "ImageSelector", "MediaSelector", "AttachmentUploader",
        "CustomSelector", "Tag", "ContentTypeFilter", "PrincipalSelector",
      ],
    )
    fun `parse input types as strings`(type: String) {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "$type"
            name: "value"
            label: "Value"
            occurrences:
              min: 0
              max: 0
            """,
          ),
        )

      assertEquals(StringField("value", "Value", true, true), result)
    }

    @Test
    fun `parse Date, Time, DateTime and Instant with regexp`() {
      mapOf("Date" to REGEX_DATE, "Time" to REGEX_TIME, "DateTime" to REGEX_DATETIME, "Instant" to REGEX_DATETIME)
        .forEach { (type, regexp) ->
          val result =
            parseInput(
              input(
                // language=YAML
                """
                type: "$type"
                name: "value"
                label: "Value"
                occurrences:
                  min: 1
                  max: 1
                """,
              ),
            )

          assertEquals(StringFieldWithValidation("value", "Value", false, false, regexp, null), result, type)
        }
    }
  }

  @Nested
  @DisplayName("CheckBox")
  inner class CheckBox {
    @Test
    fun `parse checkbox`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "checkbox"
            name: "checkbox"
            label: "checkbox test"
            """,
          ),
        )

      assertEquals(BooleanField("checkbox", "checkbox test", false, false), result)
    }

    @Test
    fun `parse CheckBox as required and single, regardless of occurrences`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "CheckBox"
            name: "checkbox"
            label: "Check box"
            occurrences:
              min: 0
              max: 0
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
          input(
            // language=YAML
            """
            type: "ComboBox"
            name: "invite"
            label: "Invited"
            occurrences:
              min: 0
              max: 1
            options:
              - value: "Yes"
                label: "Yes"
              - value: "No"
                label: "No"
              - value: "what"
                label: "Maybe"
            """,
          ),
        )

      assertEquals(UnionOfStringLiteralField("invite", "Invited", true, false, listOf("Yes", "No", "what")), result)
    }
  }

  @Nested
  @DisplayName("RadioButton")
  inner class RadioButton {
    @Test
    fun `parse RadioButton as single, regardless of occurrences`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "RadioButton"
            name: "answer"
            label: "Answer"
            occurrences:
              min: 1
              max: 0
            options:
              - value: "yes"
                label: "Yes"
              - value: "no"
                label: "No"
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
          input(
            // language=YAML
            """
            type: "Long"
            name: "year"
            label: "Year"
            occurrences:
              min: 0
              max: 1
            """,
          ),
        )

      assertEquals(NumberField("year", "Year", true, false), result)
    }

    @Test
    fun `parse Double with min and max`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "Double"
            name: "rating"
            label: "Rating"
            occurrences:
              min: 1
              max: 0
            min: 1
            max: 5
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
          input(
            // language=YAML
            """
            type: "MyCustomInput"
            name: "custom"
            label: "Custom"
            """,
          ),
        )

      assertEquals(UnknownField("custom", "Custom", true, false), result)
    }

    @Test
    fun `parse input without label`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            name: "title"
            """,
          ),
        )

      assertEquals(StringField("title", null, true, false), result)
    }

    @Test
    fun `ignore input without name`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            type: "TextLine"
            label: "Title"
            """,
          ),
        )

      assertNull(result)
    }

    @Test
    fun `ignore input without type`() {
      val result =
        parseInput(
          input(
            // language=YAML
            """
            name: "title"
            label: "Title"
            """,
          ),
        )

      assertNull(result)
    }
  }
}
