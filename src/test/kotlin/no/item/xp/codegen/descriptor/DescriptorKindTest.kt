package no.item.xp.codegen.descriptor

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DescriptorKindTest {
  @Test
  fun `accept descriptors in XP directories`() {
    mapOf(
      "cms/content-types/article/article.yaml" to DescriptorKind.CONTENT_TYPE,
      "cms/form-fragments/link/link.yaml" to DescriptorKind.FORM_FRAGMENT,
      "cms/mixins/seo/seo.yml" to DescriptorKind.MIXIN,
      "cms/pages/default/default.yaml" to DescriptorKind.PAGE,
      "cms/parts/article-view/article-view.yaml" to DescriptorKind.PART,
      "cms/layouts/two-columns/two-columns.yaml" to DescriptorKind.LAYOUT,
      "cms/macros/youtube/youtube.yaml" to DescriptorKind.MACRO,
      "cms/cms.yaml" to DescriptorKind.CMS,
      "tasks/cleanup/cleanup.yaml" to DescriptorKind.TASK,
      "idprovider/idprovider.yaml" to DescriptorKind.ID_PROVIDER,
      "admin/tools/my-tool/my-tool.yaml" to DescriptorKind.ADMIN_TOOL,
      "admin/extensions/my-extension/my-extension.yaml" to DescriptorKind.ADMIN_EXTENSION,
      "apis/search/search.yaml" to DescriptorKind.API,
    ).forEach { (path, kind) -> assertEquals(kind, DescriptorKind.of(path), path) }
  }

  @Test
  fun `reject other files`() {
    listOf(
      "enonic.yaml",
      "cms/site.yaml",
      "cms/style/style.yaml",
      "webapp/webapp.yaml",
      "cms/content-types/article/other-name.yaml",
      "cms/content-types/article/article.xml",
      "cms/content-types/article.yaml",
      "site/content-types/article/article.yaml",
      "META-INF/maven/no.item/app/pom.yaml",
      "lib/my-lib/my-lib.yaml",
      "assets/cms/content-types/article/article.yaml",
    ).forEach { assertNull(DescriptorKind.of(it), it) }
  }
}
