package no.item.xp.plugin.util

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsDescriptorInJarTest {
  @Test
  fun `accept descriptors in XP directories`() {
    listOf(
      "site/content-types/article/article.xml",
      "site/site.xml",
      "admin/tools/my-tool/my-tool.xml",
      "admin/widgets/my-widget/my-widget.xml",
      "services/search/search.xml",
      "tasks/cleanup/cleanup.xml",
      "idprovider/idprovider.xml",
    ).forEach { assertTrue(isDescriptorInJar(it), it) }
  }

  @Test
  fun `reject other files`() {
    listOf(
      "site/styles.xml",
      "application.xml",
      "META-INF/maven/no.item/app/pom.xml",
      "lib/my-lib/config.xml",
      "sites/content-types/article/article.xml",
      "site/content-types/article/article.html",
    ).forEach { assertFalse(isDescriptorInJar(it), it) }
  }
}
