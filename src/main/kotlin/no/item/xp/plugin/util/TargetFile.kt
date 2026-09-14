package no.item.xp.plugin.util

import java.io.File

val IS_CONTENT_TYPE = "^.*content-types.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_XDATA = "^.*site/x-data.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_MIXIN = "^.*site/mixins.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PART = "^.*site/parts.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PAGE = "^.*site/pages.*\$".toRegex(RegexOption.IGNORE_CASE)

fun writeTargetFile(
  targetFile: File,
  fileContent: String,
  prependText: String,
  singleQuote: Boolean,
) {
  val content = prependWithText(replaceWithSingleQuotes(fileContent, singleQuote), prependText)
  targetFile.parentFile.mkdirs()
  targetFile.createNewFile()
  targetFile.writeText(content, Charsets.UTF_8)
}

private fun replaceWithSingleQuotes(
  content: String,
  singleQuote: Boolean,
): String =
  if (singleQuote) {
    content.replace(
      "\"",
      "'",
    )
  } else {
    content
  }

private fun prependWithText(
  content: String,
  prependText: String,
): String = if (prependText.isEmpty()) content else prependText + "\n" + content

fun getTargetDirectory(
  inputFile: File,
  rootDir: File,
): File {
  return File(rootDir.absolutePath + inputFile.parentFile.absolutePath.substringAfter("resources"))
}

fun getTargetFile(
  inputFile: File,
  rootDir: File,
): File {
  return File(getTargetDirectory(inputFile, rootDir), "index.d.ts")
}

/**
 * Returns the relative import path from the directory of [targetFile] to the generated mixins in [rootDir]
 */
fun resolveMixinsImportPath(
  targetFile: File,
  rootDir: File,
): String {
  val mixinsDir = File(rootDir.absoluteFile, concatFileName("site", "mixins")).toPath()
  val targetDir = targetFile.absoluteFile.parentFile.toPath()
  val relativePath = normalizeFilePath(targetDir.relativize(mixinsDir).toString())

  return if (relativePath.startsWith(".")) relativePath else "./$relativePath"
}

fun simpleFilePath(file: File): String = file.canonicalPath.substringAfter("""resources${File.separatorChar}""")

fun normalizeFilePath(filePath: String): String = filePath.replace(File.separatorChar, '/')

fun normalizeFilePath(file: File): String = normalizeFilePath(file.absolutePath)

fun concatFileName(vararg parts: String): String = parts.joinToString(File.separator) { it }
