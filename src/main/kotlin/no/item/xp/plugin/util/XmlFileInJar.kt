package no.item.xp.plugin.util

import java.io.InputStream
import java.nio.file.Paths
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import kotlin.io.path.nameWithoutExtension

data class XmlFileInJar(val jarFile: JarFile, val entry: ZipEntry) {
  val nameWithoutExtension = Paths.get(this.entry.name).fileName.nameWithoutExtension

  fun inputStream(): InputStream = this.jarFile.getInputStream(this.entry)
}
