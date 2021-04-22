package no.item.xp.plugin.renderers.ts

const val INDENTATION_TO_SPACES = "  "

fun createIndentation(indentLevel: Int): String = INDENTATION_TO_SPACES.repeat(indentLevel)

fun escapeName(name: String): String =
  if (name.contains('-')) {
    "\"$name\""
  } else {
    name
  }

fun joinOptionList(optionList: List<String>, prefix: CharSequence = "", postfix: CharSequence = "") =
  optionList.joinToString(" | ", prefix, postfix) { "\"$it\"" }

fun renderComment(comment: String?, indentLevel: Int): String {
  val indentation = createIndentation(indentLevel)

  return if (comment != null) {
    val commentLines = comment
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

fun getInterfaceName(nameWithoutExtension: String): String {
  return nameWithoutExtension
    .split(".")[0]
    .split("-")
    .joinToString("") { it.capitalize() }
}
