package no.item.xp.plugin.util

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class ResolveMixinsImportPathTest {
  private val rootDir = File(".xp-codegen").absoluteFile

  @Test
  fun `resolve from content type`() {
    val targetFile = File(rootDir, concatFileName("site", "content-types", "article", "index.d.ts"))

    assertEquals("../../mixins", resolveMixinsImportPath(targetFile, rootDir))
  }

  @Test
  fun `resolve from mixin`() {
    val targetFile = File(rootDir, concatFileName("site", "mixins", "link", "index.d.ts"))

    assertEquals("..", resolveMixinsImportPath(targetFile, rootDir))
  }

  @Test
  fun `resolve from site config`() {
    val targetFile = File(rootDir, concatFileName("site", "index.d.ts"))

    assertEquals("./mixins", resolveMixinsImportPath(targetFile, rootDir))
  }
}
