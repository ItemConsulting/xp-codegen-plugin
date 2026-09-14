package no.item.xp.plugin.renderers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderGlobalContentTypeMapTest {
  @Test
  fun `render content type map with app name`() {
    val result = renderGlobalContentTypeMap(listOf("article", "jar-article"), "com.example")

    assertEquals(
      //language=TypeScript
      """
      #export type Article = import("./article").Article;
      #export type JarArticle = import("./jar-article").JarArticle;
      #
      #declare global {
      #  namespace XP {
      #    interface ContentTypes {
      #      "com.example:article": Article;
      #      "com.example:jar-article": JarArticle;
      #    }
      #  }
      #}
      #""".trimMargin(
        //language=
        "#",
      ),
      result,
    )
  }

  @Test
  fun `render content type map without app name`() {
    val result = renderGlobalContentTypeMap(listOf("article"), null)

    assertEquals("""export type Article = import("./article").Article;""", result)
  }
}
