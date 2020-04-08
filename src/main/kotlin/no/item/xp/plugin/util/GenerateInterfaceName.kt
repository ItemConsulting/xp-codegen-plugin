package no.item.xp.plugin.util

import java.io.File

fun generateFilePathForInterface(fileName: File): String {
  val path: String = getPathFromFile(fileName.absolutePath)
  val typeScriptFilename: String = fileName.nameWithoutExtension
  val newFilePath = "$path\\$typeScriptFilename.ts"
  File(newFilePath).createNewFile()
  return newFilePath
}

fun getPathFromFile(filename: String): String {
  val index: Int = filename.lastIndexOf(File.separator)
  return if (index > -1) filename.substring(0, index) else filename
}