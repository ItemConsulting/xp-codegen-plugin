package no.item.xp.codegen.parse

import no.item.xp.codegen.CyclicFormFragments
import no.item.xp.codegen.DuplicateFieldNames
import no.item.xp.codegen.formItems
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.TypeModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ResolveFormFragmentsTest {
  private fun descriptor(
    name: String,
    yaml: String,
  ) = FormFragmentDescriptor(name, "cms/form-fragments/$name/$name.yaml", formItems(yaml))

  @Test
  fun `resolve form fragment graph`() {
    val descriptors =
      listOf(
        descriptor(
          "aa",
          // language=YAML
          """
          kind: "FormFragment"
          title: "AA"
          form:
            - type: "TextLine"
              name: "isA"
              label: "Is A"
              occurrences:
                min: 0
                max: 1
            - include: "cc"
          """,
        ),
        descriptor(
          "bb",
          // language=YAML
          """
          kind: "FormFragment"
          title: "BB"
          form:
            - type: "TextLine"
              name: "isB"
              label: "Is B"
              occurrences:
                min: 0
                max: 1
          """,
        ),
        descriptor(
          "cc",
          // language=YAML
          """
          kind: "FormFragment"
          title: "CC"
          form:
            - type: "TextLine"
              name: "isC"
              label: "Is C"
              occurrences:
                min: 0
                max: 1
            - type: "FormFragment"
              name: "bb"
          """,
        ),
      )

    assertEquals(
      mapOf(
        "aa" to
          TypeModel(
            "aa",
            listOf(
              StringField("isA", "Is A", true, false),
              StringField("isC", "Is C", true, false),
              StringField("isB", "Is B", true, false),
            ),
          ),
        "bb" to TypeModel("bb", listOf(StringField("isB", "Is B", true, false))),
        "cc" to
          TypeModel(
            "cc",
            listOf(
              StringField("isC", "Is C", true, false),
              StringField("isB", "Is B", true, false),
            ),
          ),
      ),
      resolveFormFragments(descriptors).fragments,
    )
  }

  @Test
  fun `ignore missing form fragments`() {
    val descriptors =
      listOf(
        descriptor(
          "aa",
          // language=YAML
          """
          form:
            - type: "TextLine"
              name: "title"
            - include: "missing"
          """,
        ),
      )

    assertEquals(
      mapOf("aa" to TypeModel("aa", listOf(StringField("title", null, true, false)))),
      resolveFormFragments(descriptors).fragments,
    )
  }

  @Test
  fun `fail if form fragments have cyclic dependencies`() {
    val descriptors =
      listOf(
        descriptor(
          "first",
          // language=YAML
          """
          form:
            - include: "second"
          """,
        ),
        descriptor(
          "second",
          // language=YAML
          """
          form:
            - type: "ItemSet"
              name: "nested"
              items:
                - include: "first"
          """,
        ),
      )

    val resolved = resolveFormFragments(descriptors)

    // The cycle is only reported once, even if both form fragments are in it
    assertEquals(listOf(CyclicFormFragments(listOf("first", "second", "first"))), resolved.errors)
    assertEquals(
      "Form fragments have cyclic dependencies and cannot be resolved: first -> second -> first",
      resolved.errors.single().message,
    )
    assertEquals(setOf("first", "second"), resolved.failed)
  }

  @Test
  fun `report all invalid form fragments and resolve the valid ones`() {
    val descriptors =
      listOf(
        descriptor(
          "dependent",
          // language=YAML
          """
          form:
            - include: "invalid-a"
          """,
        ),
        descriptor(
          "invalid-a",
          // language=YAML
          """
          form:
            - type: "TextLine"
              name: "title"
            - type: "TextArea"
              name: "title"
          """,
        ),
        descriptor(
          "invalid-b",
          // language=YAML
          """
          form:
            - type: "TextLine"
              name: "name"
            - type: "TextLine"
              name: "name"
          """,
        ),
        descriptor(
          "valid",
          // language=YAML
          """
          form:
            - type: "TextLine"
              name: "title"
          """,
        ),
      )

    val resolved = resolveFormFragments(descriptors)

    assertEquals(
      listOf(
        DuplicateFieldNames(listOf("title"), "cms/form-fragments/invalid-a/invalid-a.yaml"),
        DuplicateFieldNames(listOf("name"), "cms/form-fragments/invalid-b/invalid-b.yaml"),
      ),
      resolved.errors,
    )
    assertEquals(setOf("dependent", "invalid-a", "invalid-b"), resolved.failed)
    assertEquals(mapOf("valid" to TypeModel("valid", listOf(StringField("title", null, true, false)))), resolved.fragments)
  }
}
