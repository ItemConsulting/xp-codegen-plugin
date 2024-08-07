package no.item.xp.plugin.renderers.ts

import java.util.Locale

const val INDENTATION_TO_SPACES = "  "

fun createIndentation(indentLevel: Int): String = INDENTATION_TO_SPACES.repeat(indentLevel)

fun escapeName(name: String): String =
  if (name.contains('-')) {
    "\"$name\""
  } else {
    name
  }

fun joinOptionList(
  optionList: List<String>,
  prefix: CharSequence = "",
  postfix: CharSequence = "",
) = optionList.distinct().joinToString(" | ", prefix, postfix) { "\"$it\"" }

fun renderComment(
  comment: String?,
  indentLevel: Int,
): String {
  val indentation = createIndentation(indentLevel)

  return if (comment != null) {
    val commentLines =
      comment
        .split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString("\n$indentation * ")

    """
    #$indentation/**
    #$indentation * $commentLines
    #$indentation */
    """.trimMargin("#")
  } else {
    ""
  }
}

fun getTypeName(nameWithoutExtension: String): String {
  if (nameWithoutExtension.first().isDigit()) {
    return getTypeName("_$nameWithoutExtension")
  }

  return nameWithoutExtension
    .split(".")[0]
    .split("-")
    .joinToString("") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
}
