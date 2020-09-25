package no.item.xp.plugin.util

import java.io.File

val IS_MIXIN = "^.*site/mixins.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_LAYOUT = "^.*layouts.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PART = "^.*site/parts.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PAGE = "^.*site/pages.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_SITE = "^.*site/site.xml\$".toRegex(RegexOption.IGNORE_CASE)
val IS_ERROR = "^.*site/error/error.xml\$".toRegex(RegexOption.IGNORE_CASE)
val IS_MACRO = "^.*site/macros.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_ID_PROVIDER = "^.*idprovider.*\$".toRegex(RegexOption.IGNORE_CASE)

fun getTargetFile(inputFile: File): File {
  return File(inputFile.parent, inputFile.nameWithoutExtension + getTargetFilePostfix(inputFile) + ".ts")
}

fun getTargetFilePostfix(file: File): String =
  when {
    IS_LAYOUT.matches(file.absolutePath) -> "-config"
    IS_PART.matches(file.absolutePath) -> "-part-config"
    IS_PAGE.matches(file.absolutePath) -> "-page-config"
    IS_SITE.matches(file.absolutePath) -> "-config"
    IS_MACRO.matches(file.absolutePath) -> "-config"
    IS_ID_PROVIDER.matches(file.absolutePath) -> "-config"
    IS_ERROR.matches(file.absolutePath) -> "-config"
    else -> ""
  }

fun simpleFilePath(file: File): String {
  return file.canonicalPath.substringAfter("resources/")
}
