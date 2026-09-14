package no.item.xp.plugin.renderers.ts

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TSRenderUtilsTest {
  @Test
  fun `create type name from file name`() {
    mapOf(
      "article" to "Article",
      "my-content-type" to "MyContentType",
      "2-columns" to "_2Columns",
      "article.xml" to "Article",
      "alreadyCamelCase" to "AlreadyCamelCase",
    ).forEach { (name, expected) -> assertEquals(expected, getTypeName(name), name) }
  }

  @Test
  fun `escape names with hyphens`() {
    assertEquals("\"my-field\"", escapeName("my-field"))
    assertEquals("myField", escapeName("myField"))
  }

  @Test
  fun `render comment`() {
    assertEquals(
      """
      #  /**
      #   * First line
      #   * Second line
      #   */
      """.trimMargin("#"),
      renderComment("\n  First line\n    Second line  \n", 1),
    )
  }

  @Test
  fun `render no comment`() {
    assertEquals("", renderComment(null, 1))
  }
}
