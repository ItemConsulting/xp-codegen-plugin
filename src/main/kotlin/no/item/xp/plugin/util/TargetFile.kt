package no.item.xp.plugin.util

import java.io.File

const val OUTPUT_FILE_HEADER_WARNING =
  """WARNING: This file was automatically generated by "no.item.xp.codegen". You may lose your changes if you edit it."""

val IS_MIXIN = "^.*site/mixins.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_LAYOUT = "^.*layouts.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PART = "^.*site/parts.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_PAGE = "^.*site/pages.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_SITE = "^.*site/site.xml\$".toRegex(RegexOption.IGNORE_CASE)
val IS_ERROR = "^.*site/error/error.xml\$".toRegex(RegexOption.IGNORE_CASE)
val IS_MACRO = "^.*site/macros.*\$".toRegex(RegexOption.IGNORE_CASE)
val IS_ID_PROVIDER = "^.*idprovider.*\$".toRegex(RegexOption.IGNORE_CASE)

enum class FileType(val filePostfix: String) {
  TypeScriptDeclaration(".d.ts"),
  TypeScript(".ts"),
  JSDoc(".js"),
  IoTs(".ts"),
  None("")
}

fun getTargetFile(inputFile: File, filePostfix: String): File =
  File(inputFile.parent, inputFile.nameWithoutExtension + getTargetFilePostfix(inputFile) + filePostfix)

fun getTargetFile(inputFile: File, fileType: FileType): File =
  getTargetFile(inputFile, fileType.filePostfix)

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

fun simpleFilePath(file: File): String =
  file.canonicalPath.substringAfter("resources/")
