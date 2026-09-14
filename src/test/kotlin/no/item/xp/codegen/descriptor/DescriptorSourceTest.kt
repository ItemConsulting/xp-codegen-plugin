package no.item.xp.codegen.descriptor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.test.assertEquals

class DescriptorSourceTest {
  @TempDir
  lateinit var tempDir: File

  @Test
  fun `include jars with the same file name`() {
    val first = createJar("first/core.jar", "cms/content-types/article/article.yaml" to "title: \"First\"")
    val second = createJar("second/core.jar", "cms/content-types/event/event.yaml" to "title: \"Second\"")

    assertEquals(
      listOf(
        JarDescriptorSource("cms/content-types/article/article.yaml", DescriptorKind.CONTENT_TYPE, first),
        JarDescriptorSource("cms/content-types/event/event.yaml", DescriptorKind.CONTENT_TYPE, second),
      ),
      collectDescriptorSources(emptyMap(), listOf(first, second)),
    )
  }

  @Test
  fun `use the descriptor in the first jar if several jars have the same path`() {
    val first = createJar("first.jar", "cms/content-types/article/article.yaml" to "title: \"First\"")
    val second = createJar("second.jar", "cms/content-types/article/article.yml" to "title: \"Second\"")

    val sources = collectDescriptorSources(emptyMap(), listOf(first, second))

    assertEquals(listOf("title: \"First\""), sources.map { it.readText() })
  }

  @Test
  fun `only read a jar once`() {
    val jar = createJar("core.jar", "cms/content-types/article/article.yaml" to "title: \"Article\"")

    assertEquals(1, collectDescriptorSources(emptyMap(), listOf(jar, File(tempDir, "./core.jar"))).size)
  }

  @Test
  fun `use the descriptor in the project instead of the jar`() {
    val jar = createJar("core.jar", "cms/content-types/article/article.yaml" to "title: \"Jar\"")
    val file = File(tempDir, "article.yaml").apply { writeText("title: \"Project\"") }

    assertEquals(
      listOf(FileDescriptorSource("cms/content-types/article/article.yaml", DescriptorKind.CONTENT_TYPE, file)),
      collectDescriptorSources(mapOf("cms/content-types/article/article.yaml" to file), listOf(jar)),
    )
  }

  private fun createJar(
    path: String,
    vararg entries: Pair<String, String>,
  ): File {
    val jarFile = File(tempDir, path).apply { parentFile.mkdirs() }

    JarOutputStream(jarFile.outputStream()).use { jar ->
      entries.forEach { (name, content) ->
        jar.putNextEntry(JarEntry(name))
        jar.write(content.toByteArray(Charsets.UTF_8))
        jar.closeEntry()
      }
    }

    return jarFile
  }
}
