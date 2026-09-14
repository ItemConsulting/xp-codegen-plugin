package no.item.xp.codegen.render

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CodeWriterTest {
  private fun CodeWriter.writeExample() =
    apply {
      block("declare global {", "}") {
        comment("A map")
        block("interface Map {", "}") {
          line("key: string;")
          line()
          indent(2) { line("deep: true;") }
        }
      }
    }

  @Test
  fun `indent with two spaces by default`() {
    assertEquals(
      """
      #declare global {
      #  /**
      #   * A map
      #   */
      #  interface Map {
      #    key: string;
      #
      #        deep: true;
      #  }
      #}
      #""".trimMargin("#"),
      CodeWriter().writeExample().toString(),
    )
  }

  @Test
  fun `indent with four spaces`() {
    assertEquals(
      """
      #declare global {
      #    /**
      #     * A map
      #     */
      #    interface Map {
      #        key: string;
      #
      #                deep: true;
      #    }
      #}
      #""".trimMargin("#"),
      CodeWriter("    ").writeExample().toString(),
    )
  }

  @Test
  fun `indent with tabs`() {
    assertEquals(
      "declare global {\n\t/**\n\t * A map\n\t */\n\tinterface Map {\n\t\tkey: string;\n\n\t\t\t\tdeep: true;\n\t}\n}\n",
      CodeWriter("\t").writeExample().toString(),
    )
  }

  @Test
  fun `render comment with trimmed lines`() {
    assertEquals(
      """
      #/**
      # * First line
      # * Second line
      # */
      #""".trimMargin("#"),
      CodeWriter().comment("\n  First line\n    Second line  \n").toString(),
    )
  }

  @Test
  fun `render no comment`() {
    assertEquals("", CodeWriter().comment(null).toString())
    assertEquals("", CodeWriter().comment(" \n ").toString())
  }

  @Test
  fun `separate items with empty lines`() {
    assertEquals("a\n\nb\n\nc\n", CodeWriter().separated(listOf("a", "b", "c")) { line(it) }.toString())
  }
}
