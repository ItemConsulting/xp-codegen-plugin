package no.item.xp.codegen.render

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderIndexTest {
  @Test
  fun `render content type index with app name`() {
    val result = renderContentTypeIndex(listOf("article", "jar-article"), "com.example")

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
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render content type index without app name`() {
    val result = renderContentTypeIndex(listOf("article"), null)

    assertEquals("export type Article = import(\"./article\").Article;\n", result)
  }

  @Test
  fun `render component index with app name`() {
    val result = renderComponentIndex(listOf("article-view"), "com.example", "PartMap", "XpPartMap", false)

    assertEquals(
      //language=TypeScript
      """
      #export type ArticleView = import("./article-view").ArticleView;
      #
      #export type PartMap = {
      #  "com.example:article-view": ArticleView;
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render component index with global declarations`() {
    val result = renderComponentIndex(listOf("default"), "com.example", "PageMap", "XpPageMap", true)

    assertEquals(
      //language=TypeScript
      """
      #export type Default = import("./default").Default;
      #
      #export type PageMap = {
      #  "com.example:default": Default;
      #};
      #
      #declare global {
      #  interface XpPageMap {
      #    "com.example:default": Default;
      #  }
      #}
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render component index without app name`() {
    val result = renderComponentIndex(listOf("default"), null, "PageMap", "XpPageMap", true)

    assertEquals("export type Default = import(\"./default\").Default;\n", result)
  }

  @Test
  fun `render mixin index with global declarations`() {
    val result = renderMixinIndex(listOf("menu-item", "seo"), "no.item.example", true)

    assertEquals(
      //language=TypeScript
      """
      #export type MenuItem = import("./menu-item").MenuItem;
      #export type Seo = import("./seo").Seo;
      #
      #export type MixinMap = {
      #  "no-item-example"?: {
      #    "menu-item"?: MenuItem;
      #    seo?: Seo;
      #  };
      #};
      #
      #declare global {
      #  interface XpMixin {
      #    "no-item-example"?: {
      #      "menu-item"?: MenuItem;
      #      seo?: Seo;
      #    };
      #  }
      #}
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render index with tabs`() {
    val result = renderComponentIndex(listOf("default"), "app", "PageMap", "XpPageMap", false, "\t")

    assertEquals(
      "export type Default = import(\"./default\").Default;\n\nexport type PageMap = {\n\t\"app:default\": Default;\n};\n",
      result,
    )
  }
}
