package no.item.xp.codegen.validation

import no.item.xp.codegen.descriptor.DescriptorKind
import no.item.xp.codegen.yaml
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class SchemaValidatorTest {
  @Test
  fun `accept valid content type`() {
    val descriptor =
      yaml(
        // language=YAML
        """
        kind: "ContentType"
        title: "Article"
        superType: "base:structured"
        form:
          - type: "TextLine"
            name: "title"
            label:
              text: "Title"
              i18n: "article.title"
            occurrences:
              min: 1
              max: 1
            maxLength: 100
          - include: "link"
          - type: "OptionSet"
            name: "type"
            label: "Type"
            selection:
              min: 1
              max: 1
            options:
              - name: "a"
                label: "A"
        """,
      )

    val result = SchemaValidator.validate(DescriptorKind.CONTENT_TYPE, "cms/content-types/article/article.yaml", descriptor)

    assertTrue(result.isRight(), result.leftOrNull()?.message)
  }

  @Test
  fun `reject invalid content type`() {
    val descriptor =
      yaml(
        // language=YAML
        """
        kind: "ContentType"
        form:
          - type: "TextLine"
            name: "title"
            label: "Title"
            unknownProperty: true
        """,
      )

    val error = SchemaValidator.validate(DescriptorKind.CONTENT_TYPE, "cms/content-types/article/article.yaml", descriptor).leftOrNull()

    assertTrue(error != null && error.errors.isNotEmpty())
    assertContains(error.message, "\"cms/content-types/article/article.yaml\" is not a valid descriptor")
    assertContains(error.message, "title")
  }

  @Test
  fun `reject descriptor of wrong kind`() {
    val descriptor =
      yaml(
        // language=YAML
        """
        kind: "Part"
        title: "Article view"
        """,
      )

    assertTrue(SchemaValidator.validate(DescriptorKind.PAGE, "cms/pages/article-view/article-view.yaml", descriptor).isLeft())
  }
}
