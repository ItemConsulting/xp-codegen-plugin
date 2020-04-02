package no.item.xp.plugin.util

import java.io.File

@Suppress("FunctionName")
fun GenerateInterfaceName(fileName: File): String? {
  val filePath: String = pathComponent(fileName.absolutePath)
  val newFileName: String = filePath + "\\" + fileName.nameWithoutExtension + ".ts"
  val file = File(newFileName)
  val isNewFileCreated: Boolean = file.createNewFile()
  return if (isNewFileCreated) {
    newFileName
  } else {
    println("$newFileName already exists.")
    null
  }
}

fun pathComponent(filename: String): String {
  val i: Int = filename.lastIndexOf(File.separator)
  return if (i > -1) filename.substring(0, i) else filename
}
