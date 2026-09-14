package no.item.xp.codegen.render

/**
 * Replaces double quotes with single quotes if [singleQuote] is true, and adds [prependText] to the top of the file
 */
fun applyOutputOptions(
  content: String,
  prependText: String,
  singleQuote: Boolean,
): String {
  val quoted = if (singleQuote) content.replace('"', '\'') else content

  return if (prependText.isEmpty()) quoted else "$prependText\n$quoted"
}
