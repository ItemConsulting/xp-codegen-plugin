package no.item.xp.plugin.util

import java.io.File

val IS_CONTENT_TYPE = "^.*content-types.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_XDATA = "^.*site/x-data.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_MIXIN = "^.*site/mixins.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PART = "^.*site/parts.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PAGE = "^.*site/pages.*\$".toRegex(RegexOption.IGNORE_CASE)

fun getTargetDirectory(inputFile: File, rootDir: File): File {
  return File(rootDir.absolutePath + inputFile.parentFile.absolutePath.substringAfter("resources"))
}

fun getTargetFile(inputFile: File, rootDir: File): File {
  return File(getTargetDirectory(inputFile, rootDir), "index.d.ts")
}

fun isContentType(file: File): Boolean = IS_CONTENT_TYPE.matches(normalizeFilePath(file))

fun getTargetFilePostfix(file: File): String =
  when {
    IS_MIXIN.matches(normalizeFilePath(file)) -> ""
    IS_CONTENT_TYPE.matches(normalizeFilePath(file)) -> ""
    IS_XDATA.matches(normalizeFilePath(file)) -> ""
    else -> "-config"
  }

fun simpleFilePath(file: File): String =
  file.canonicalPath.substringAfter("""resources${File.separatorChar}""")

fun normalizeFilePath(file: File) = file.absolutePath.replace(File.separatorChar, '/')

fun concatFileName(vararg parts: String): String = parts.joinToString(File.separator) { it }
