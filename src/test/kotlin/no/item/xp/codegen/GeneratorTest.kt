package no.item.xp.codegen

import no.item.xp.codegen.descriptor.DescriptorKind
import no.item.xp.codegen.descriptor.FileDescriptorSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals

class GeneratorTest {
  @TempDir
  lateinit var tempDir: File

  private val settings = GenerationSettings(appName = null, declareGlobals = false)

  @Test
  fun `report errors in form fragments together with errors in other descriptors`() {
    val sources =
      listOf(
        source(
          "cms/content-types/article/article.yaml",
          // language=YAML
          """
          kind: "ContentType"
          title: "Article"
          superType: "base:structured"
          form:
            - type: "TextLine"
              name: "title"
              label: "Title"
            - type: "TextArea"
              name: "title"
              label: "Title"
            - include: "cyclic"
          """,
        ),
        source(
          "cms/form-fragments/cyclic/cyclic.yaml",
          // language=YAML
          """
          kind: "FormFragment"
          title: "Cyclic"
          form:
            - include: "cyclic"
          """,
        ),
        source(
          "cms/form-fragments/intro/intro.yaml",
          // language=YAML
          """
          kind: "FormFragment"
          title: "Intro"
          form:
            - type: "TextLine"
              name: "intro"
              label: "Intro"
            - type: "TextArea"
              name: "intro"
              label: "Intro"
          """,
        ),
      )

    assertEquals(
      listOf<CodegenError>(
        CyclicFormFragments(listOf("cyclic", "cyclic")),
        DuplicateFieldNames(listOf("intro"), "cms/form-fragments/intro/intro.yaml"),
        DuplicateFieldNames(listOf("title"), "cms/content-types/article/article.yaml"),
      ),
      generate(sources, settings).leftOrNull()?.toList(),
    )
  }

  private fun source(
    path: String,
    yaml: String,
  ) = FileDescriptorSource(
    path,
    requireNotNull(DescriptorKind.of(path)),
    File(tempDir, path).apply {
      parentFile.mkdirs()
      writeText(yaml.trimIndent())
    },
  )
}
