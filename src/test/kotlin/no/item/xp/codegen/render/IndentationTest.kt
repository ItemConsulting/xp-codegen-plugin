package no.item.xp.codegen.render

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals

class IndentationTest {
  @TempDir
  lateinit var projectDir: File

  private val generatedFile: File
    get() = File(projectDir, ".xp-codegen/cms/parts/article-view/index.d.ts")

  private fun editorConfig(
    directory: File,
    content: String,
  ) {
    directory.mkdirs()
    File(directory, ".editorconfig").writeText(content.trimIndent())
  }

  private fun resolve(): String? = IndentationResolver().resolveIndentUnit(generatedFile.toPath()).getOrNull()

  @Test
  fun `use two spaces without indentation settings`() {
    editorConfig(projectDir, "root = true")

    assertEquals("  ", resolve())
  }

  @Test
  fun `use tabs`() {
    editorConfig(
      projectDir,
      """
      root = true

      [*]
      indent_style = tab
      indent_size = 4
      """,
    )

    assertEquals("\t", resolve())
  }

  @Test
  fun `use indent size from the most specific section`() {
    editorConfig(
      projectDir,
      """
      root = true

      [*]
      indent_style = space
      indent_size = 2

      [*.{ts,tsx}]
      indent_size = 4
      """,
    )

    assertEquals("    ", resolve())
  }

  @Test
  fun `use tab width when indent size is tab`() {
    editorConfig(
      projectDir,
      """
      root = true

      [*]
      indent_style = space
      indent_size = tab
      tab_width = 3
      """,
    )

    assertEquals("   ", resolve())
  }

  @Test
  fun `stop searching at root`() {
    editorConfig(
      projectDir,
      """
      [*]
      indent_style = tab
      """,
    )
    editorConfig(
      File(projectDir, ".xp-codegen"),
      """
      root = true

      [*]
      indent_size = 4
      """,
    )

    assertEquals("    ", resolve())
  }

  @Test
  fun `let nested editorconfig override parent`() {
    editorConfig(
      projectDir,
      """
      root = true

      [*]
      indent_style = space
      indent_size = 8
      """,
    )
    editorConfig(
      File(projectDir, ".xp-codegen/cms"),
      """
      [*.ts]
      indent_style = tab
      """,
    )

    assertEquals("\t", resolve())
  }
}
