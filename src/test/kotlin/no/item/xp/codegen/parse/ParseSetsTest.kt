package no.item.xp.codegen.parse

import no.item.xp.codegen.fieldSet
import no.item.xp.codegen.itemSet
import no.item.xp.codegen.model.ObjectField
import no.item.xp.codegen.model.OptionSetField
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.optionSet
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParseSetsTest {
  @Nested
  @DisplayName("FieldSet")
  inner class FieldSets {
    @Test
    fun `parse FieldSet`() {
      val result =
        parseFieldSet(
          fieldSet(
            // language=YAML
            """
            type: "FieldSet"
            label: "Contact info"
            items:
              - type: "TextLine"
                name: "email"
                label: "Email"
                occurrences:
                  min: 1
                  max: 1
              - type: "TextLine"
                name: "phoneNumber"
                label: "Phone"
                occurrences:
                  min: 1
                  max: 1
            """,
          ),
          emptyMap(),
        )

      assertEquals(
        listOf(
          StringField("email", "Email", false, false),
          StringField("phoneNumber", "Phone", false, false),
        ),
        result.getOrNull(),
      )
    }

    @Test
    fun `parse FieldSet without items`() {
      val result =
        parseFieldSet(
          fieldSet(
            // language=YAML
            """
            type: "FieldSet"
            label: "Empty"
            """,
          ),
          emptyMap(),
        )

      assertEquals(emptyList(), result.getOrNull())
    }
  }

  @Nested
  @DisplayName("ItemSet")
  inner class ItemSets {
    @Test
    fun `parse ItemSet`() {
      val result =
        parseItemSet(
          itemSet(
            // language=YAML
            """
            type: "ItemSet"
            name: "contactInfo"
            label: "Contact Info"
            occurrences:
              min: 0
              max: 0
            items:
              - type: "TextLine"
                name: "label"
                label: "Label"
                occurrences:
                  min: 0
                  max: 1
              - type: "TextLine"
                name: "phoneNumber"
                label: "Phone Number"
                occurrences:
                  min: 0
                  max: 1
            """,
          ),
          emptyMap(),
        )

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
        result.getOrNull(),
      )
    }

    @Test
    fun `parse nested ItemSet`() {
      val result =
        parseItemSet(
          itemSet(
            // language=YAML
            """
            type: "ItemSet"
            name: "contact"
            label: "Contact"
            occurrences:
              min: 1
              max: 1
            items:
              - type: "ItemSet"
                name: "phoneNumbers"
                label: "Phone numbers"
                occurrences:
                  min: 0
                  max: 0
                items:
                  - type: "TextLine"
                    name: "number"
                    label: "Number"
                    occurrences:
                      min: 1
                      max: 1
            """,
          ),
          emptyMap(),
        )

      assertEquals(
        ObjectField(
          "contact",
          "Contact",
          false,
          false,
          listOf(
            ObjectField("phoneNumbers", "Phone numbers", true, true, listOf(StringField("number", "Number", false, false))),
          ),
        ),
        result.getOrNull(),
      )
    }
  }

  @Nested
  @DisplayName("OptionSet")
  inner class OptionSets {
    @Test
    fun `parse OptionSet`() {
      val result =
        parseOptionSet(
          optionSet(
            // language=YAML
            """
            type: "OptionSet"
            name: "myOptionSet"
            label: "Select content manually?"
            occurrences:
              min: 1
              max: 1
            selection:
              min: 1
              max: 1
            options:
              - name: "no"
                label: "No"
                selected: true
              - name: "yes"
                label: "Yes"
                items:
                  - type: "ContentSelector"
                    name: "articleList"
                    label: "Select articles for the list"
                    occurrences:
                      min: 0
                      max: 0
            """,
          ),
          emptyMap(),
        )

      assertEquals(
        OptionSetField(
          "myOptionSet",
          "Select content manually?",
          false,
          false,
          false,
          listOf(
            ObjectField("no", "No", true, false, listOf()),
            ObjectField(
              "yes",
              "Yes",
              true,
              false,
              listOf(StringField("articleList", "Select articles for the list", true, true)),
            ),
          ),
        ),
        result.getOrNull(),
      )
    }

    @Test
    fun `parse multi select OptionSet`() {
      val result =
        parseOptionSet(
          optionSet(
            // language=YAML
            """
            type: "OptionSet"
            name: "multiSelect"
            label: "Multi select"
            occurrences:
              min: 0
              max: 0
            selection:
              min: 0
              max: 2
            options:
              - name: "first"
                label: "First"
            """,
          ),
          emptyMap(),
        )

      assertEquals(
        OptionSetField("multiSelect", "Multi select", true, true, true, listOf(ObjectField("first", "First", true, false, emptyList()))),
        result.getOrNull(),
      )
    }

    @Test
    fun `parse OptionSet without selection as single select`() {
      val result =
        parseOptionSet(
          optionSet(
            // language=YAML
            """
            type: "OptionSet"
            name: "singleSelect"
            label: "Single select"
            options:
              - name: "first"
                label: "First"
            """,
          ),
          emptyMap(),
        )

      assertEquals(
        OptionSetField(
          "singleSelect",
          "Single select",
          true,
          false,
          false,
          listOf(ObjectField("first", "First", true, false, emptyList())),
        ),
        result.getOrNull(),
      )
    }
  }
}
