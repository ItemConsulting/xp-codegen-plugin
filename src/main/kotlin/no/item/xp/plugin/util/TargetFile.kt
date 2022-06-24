package no.item.xp.plugin.util

import java.io.File

val IS_CONTENT_TYPE = "^.*content-types.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_XDATA = "^.*site/x-data.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_MIXIN = "^.*site/mixins.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PART = "^.*site/parts.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PAGE = "^.*site/pages.*\$".toRegex(RegexOption.IGNORE_CASE)


fun getTargetFile(inputFile: File): File =
  File(inputFile.parent, inputFile.nameWithoutExtension + getTargetFilePostfix(inputFile) + ".d.ts")

fun getTargetFilePostfix(file: File): String =
  when {
    IS_MIXIN.matches(normalizeFilePath(file)) -> ""
    IS_CONTENT_TYPE.matches(normalizeFilePath(file)) -> ""
    IS_XDATA.matches(normalizeFilePath(file)) -> ""
    IS_PART.matches(normalizeFilePath(file)) -> "-part-config"
    IS_PAGE.matches(normalizeFilePath(file)) -> "-page-config"
    else -> "-config"
  }

fun simpleFilePath(file: File): String =
  file.canonicalPath.substringAfter("""resources${File.separatorChar}""")

fun normalizeFilePath(file: File) = file.absolutePath.replace(File.separatorChar, '/')
