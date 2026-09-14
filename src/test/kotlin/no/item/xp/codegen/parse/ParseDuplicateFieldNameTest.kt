package no.item.xp.codegen.parse

import no.item.xp.codegen.DuplicateFieldNames
import no.item.xp.codegen.formItems
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.TypeModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParseDuplicateFieldNameTest {
  private val fragments = mapOf("intro" to TypeModel("intro", listOf(StringField("intro", "Intro", true, false))))

  private fun parseForm(yaml: String) = parseTypeModel("article", formItems(yaml), fragments)

  @Test
  fun `fail if form fragment contains field with same name`() {
    val result =
      parseForm(
        // language=YAML
        """
        form:
          - type: "TextLine"
            name: "intro"
            label: "Intro"
          - include: "intro"
        """,
      )

    assertEquals(DuplicateFieldNames(listOf("intro")), result.leftOrNull())
  }

  @Test
  fun `fail if field set contains field with same name`() {
    val result =
      parseForm(
        // language=YAML
        """
        form:
          - type: "ItemSet"
            name: "block"
            items:
              - type: "TextLine"
                name: "title"
                label: "Title"
              - type: "FieldSet"
                label: "Field set"
                items:
                  - type: "TextArea"
                    name: "title"
                    label: "Title"
        """,
      )

    assertEquals(DuplicateFieldNames(listOf("title")), result.leftOrNull())
  }

  @Test
  fun `allow same field name in different objects`() {
    val result =
      parseForm(
        // language=YAML
        """
        form:
          - type: "TextLine"
            name: "title"
            label: "Title"
          - type: "ItemSet"
            name: "block"
            items:
              - type: "TextLine"
                name: "title"
                label: "Title"
          - type: "OptionSet"
            name: "link"
            selection:
              min: 1
              max: 1
            options:
              - name: "internal"
                label: "Internal"
                items:
                  - type: "TextLine"
                    name: "title"
                    label: "Title"
        """,
      )

    assertEquals(3, result.getOrNull()?.fields?.size)
  }

  @Test
  fun `fail with path of form fragment that contains duplicates`() {
    val descriptors =
      listOf(
        FormFragmentDescriptor(
          "aa",
          "cms/form-fragments/aa/aa.yaml",
          formItems(
            // language=YAML
            """
            form:
              - type: "TextLine"
                name: "intro"
                label: "Intro"
              - include: "bb"
            """,
          ),
        ),
        FormFragmentDescriptor(
          "bb",
          "cms/form-fragments/bb/bb.yaml",
          formItems(
            // language=YAML
            """
            form:
              - type: "TextArea"
                name: "intro"
                label: "Intro"
            """,
          ),
        ),
      )

    assertEquals(
      "Duplicate field name \"intro\" in \"cms/form-fragments/aa/aa.yaml\". A field name can only be used once in the same object.",
      resolveFormFragments(descriptors).errors.single().message,
    )
  }
}
