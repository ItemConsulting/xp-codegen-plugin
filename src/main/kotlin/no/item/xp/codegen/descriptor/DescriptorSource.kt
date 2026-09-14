package no.item.xp.codegen.descriptor

import java.io.File
import java.util.jar.JarFile

/**
 * A YAML descriptor, either in the resources of the project or in an included jar
 */
sealed interface DescriptorSource {
  // Path relative to the resources directory (or the root of the jar), using "/" as separator
  val relativePath: String
  val kind: DescriptorKind

  val name: String
    get() = relativePath.substringAfterLast('/').substringBeforeLast('.')

  fun readText(): String
}

data class FileDescriptorSource(
  override val relativePath: String,
  override val kind: DescriptorKind,
  val file: File,
) : DescriptorSource {
  override fun readText(): String = file.readText(Charsets.UTF_8)
}

data class JarDescriptorSource(
  override val relativePath: String,
  override val kind: DescriptorKind,
  val jar: File,
) : DescriptorSource {
  override fun readText(): String =
    JarFile(jar).use { jarFile ->
      jarFile.getInputStream(jarFile.getJarEntry(relativePath)).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}

/**
 * Returns the descriptors in [resourceFiles] (relative path to file) and [jars], sorted by path. A descriptor in the
 * project replaces a descriptor with the same path in a jar.
 */
fun collectDescriptorSources(
  resourceFiles: Map<String, File>,
  jars: Collection<File>,
): List<DescriptorSource> {
  val fileSources =
    resourceFiles.mapNotNull { (path, file) ->
      DescriptorKind.of(path)?.let { FileDescriptorSource(path, it, file) }
    }

  val localPaths = fileSources.map { it.relativePath.substringBeforeLast('.') }.toSet()

  val jarSources =
    jars
      .filter { it.isFile && it.extension == "jar" }
      .distinctBy { it.name }
      .flatMap { jar ->
        JarFile(jar).use { jarFile ->
          jarFile
            .entries()
            .asSequence()
            // ZipEntry.name has UNIX style path. See 4.4.17.1 of the zip file spec.
            .filterNot { it.isDirectory }
            .mapNotNull { entry -> DescriptorKind.of(entry.name)?.let { JarDescriptorSource(entry.name, it, jar) } }
            .toList()
        }
      }.filterNot { it.relativePath.substringBeforeLast('.') in localPaths }

  return (fileSources + jarSources).sortedBy { it.relativePath }
}
