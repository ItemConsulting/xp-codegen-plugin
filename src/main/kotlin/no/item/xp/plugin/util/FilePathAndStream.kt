package no.item.xp.plugin.util

import java.io.InputStream
import java.nio.file.Paths
import kotlin.io.path.nameWithoutExtension

data class FilePathAndStream(
  val filePath: String,
  val nameWithoutExtension: String,
  val inputStream: InputStream
){
  constructor(fileInJar: XmlFileInJar) : this(
    fileInJar.entry.name,
    Paths.get(fileInJar.entry.name).fileName.nameWithoutExtension,
    fileInJar.jarFile.getInputStream(fileInJar.entry)
  )
}
