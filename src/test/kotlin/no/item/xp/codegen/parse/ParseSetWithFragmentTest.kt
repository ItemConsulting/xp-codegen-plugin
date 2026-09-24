package no.item.xp.codegen.parse

import no.item.xp.codegen.itemSet
import no.item.xp.codegen.model.ObjectField
import no.item.xp.codegen.model.OptionSetField
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.TypeModel
import no.item.xp.codegen.optionSet
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParseSetWithFragmentTest {
  private val urlField = StringField("url", "Url", false, false)
  private val titleField = StringField("title", "Title", false, false)
  private val fragments =
    mapOf(
      "link" to TypeModel("link", listOf(urlField)),
      "_title" to TypeModel("_title", listOf(titleField)),
    )

  @Test
  fun `parse ItemSet with only a form fragment`() {
    val result =
      parseItemSet(
        itemSet(
          // language=YAML
          """
          type: "ItemSet"
          name: "links"
          label: "Links"
          occurrences:
            min: 0
            max: 0
          items:
            - include: "link"
          """,
        ),
        fragments,
      )

    assertEquals(ObjectField("links", "Links", true, true, listOf(urlField), listOf("link")), result.getOrNull())
  }

  @Test
  fun `parse ItemSet with only a form fragment referenced by type`() {
    val result =
      parseItemSet(
        itemSet(
          // language=YAML
          """
          type: "ItemSet"
          name: "links"
          label: "Links"
          items:
            - type: "FormFragment"
              name: "link"
          """,
        ),
        fragments,
      )

    assertEquals(ObjectField("links", "Links", true, false, listOf(urlField), listOf("link")), result.getOrNull())
  }

  @Test
  fun `parse ItemSet with only form fragments`() {
    val result =
      parseItemSet(
        itemSet(
          // language=YAML
          """
          type: "ItemSet"
          name: "links"
          label: "Links"
          items:
            - include: "link"
            - type: "FormFragment"
              name: "_title"
          """,
        ),
        fragments,
      )

    assertEquals(
      ObjectField("links", "Links", true, false, listOf(urlField, titleField), listOf("link", "_title")),
      result.getOrNull(),
    )
  }

  @Test
  fun `parse ItemSet with only form fragments where one is unknown`() {
    val result =
      parseItemSet(
        itemSet(
          // language=YAML
          """
          type: "ItemSet"
          name: "links"
          label: "Links"
          items:
            - include: "link"
            - include: "missing"
          """,
        ),
        fragments,
      )

    assertEquals(ObjectField("links", "Links", true, false, listOf(urlField)), result.getOrNull())
  }

  @Test
  fun `parse ItemSet with a form fragment and other fields`() {
    val result =
      parseItemSet(
        itemSet(
          // language=YAML
          """
          type: "ItemSet"
          name: "links"
          label: "Links"
          occurrences:
            min: 0
            max: 0
          items:
            - type: "TextLine"
              name: "title"
              label: "Title"
              occurrences:
                min: 0
                max: 1
            - include: "link"
          """,
        ),
        fragments,
      )

    assertEquals(
      ObjectField("links", "Links", true, true, listOf(StringField("title", "Title", true, false), urlField)),
      result.getOrNull(),
    )
  }

  @Test
  fun `parse ItemSet with only an unknown form fragment`() {
    val result =
      parseItemSet(
        itemSet(
          // language=YAML
          """
          type: "ItemSet"
          name: "links"
          label: "Links"
          occurrences:
            min: 0
            max: 0
          items:
            - include: "missing"
          """,
        ),
        fragments,
      )

    assertEquals(ObjectField("links", "Links", true, true, emptyList()), result.getOrNull())
  }

  @Test
  fun `parse OptionSet option with only a form fragment`() {
    val result =
      parseOptionSet(
        optionSet(
          // language=YAML
          """
          type: "OptionSet"
          name: "myLink"
          label: "Link"
          occurrences:
            min: 1
            max: 1
          selection:
            min: 1
            max: 1
          options:
            - name: "none"
              label: "None"
            - name: "internal"
              label: "Internal"
              items:
                - include: "link"
          """,
        ),
        fragments,
      )

    assertEquals(
      OptionSetField(
        "myLink",
        "Link",
        false,
        false,
        false,
        listOf(
          ObjectField("none", "None", true, false, emptyList()),
          ObjectField("internal", "Internal", true, false, listOf(urlField), listOf("link")),
        ),
      ),
      result.getOrNull(),
    )
  }
}
