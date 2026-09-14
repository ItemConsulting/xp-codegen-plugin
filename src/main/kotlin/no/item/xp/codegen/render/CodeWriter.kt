package no.item.xp.codegen.render

const val DEFAULT_INDENT_UNIT = "  "

/**
 * Writes code line by line, and keeps track of the indentation. [indentUnit] is written once per indentation level.
 */
class CodeWriter(
  private val indentUnit: String = DEFAULT_INDENT_UNIT,
) {
  private val out = StringBuilder()
  private var level = 0

  /**
   * Writes [text] as an indented line. An empty [text] is written as an empty line without indentation.
   */
  fun line(text: String = ""): CodeWriter =
    apply {
      if (text.isNotEmpty()) {
        out.append(indentUnit.repeat(level)).append(text)
      }
      out.append('\n')
    }

  /**
   * Writes the lines in [body] with [levels] more indentation
   */
  fun indent(
    levels: Int = 1,
    body: CodeWriter.() -> Unit,
  ): CodeWriter =
    apply {
      level += levels
      body()
      level -= levels
    }

  /**
   * Writes [open], the lines in [body] with one more level of indentation, and then [close]
   */
  fun block(
    open: String,
    close: String,
    body: CodeWriter.() -> Unit,
  ): CodeWriter =
    apply {
      line(open)
      indent(body = body)
      line(close)
    }

  /**
   * Writes a JSDoc comment. Nothing is written if [text] has no content.
   */
  fun comment(text: String?): CodeWriter =
    apply {
      val lines =
        text
          ?.lines()
          ?.map { it.trim() }
          ?.filter { it.isNotEmpty() }
          .orEmpty()

      if (lines.isNotEmpty()) {
        line("/**")
        lines.forEach { line(" * $it") }
        line(" */")
      }
    }

  /**
   * Writes every item in [items] with [render], separated by empty lines
   */
  fun <T> separated(
    items: List<T>,
    render: CodeWriter.(T) -> Unit,
  ): CodeWriter =
    apply {
      items.forEachIndexed { index, item ->
        if (index > 0) {
          line()
        }
        render(item)
      }
    }

  override fun toString(): String = out.toString()
}
