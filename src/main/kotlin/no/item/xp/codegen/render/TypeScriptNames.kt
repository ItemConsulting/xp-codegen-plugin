package no.item.xp.codegen.render

import java.util.Locale

/**
 * Returns a TypeScript type name based on the name of a descriptor. E.g. "my-content-type" becomes "MyContentType".
 */
fun getTypeName(name: String): String {
  if (name.firstOrNull()?.isDigit() == true) {
    return getTypeName("_$name")
  }

  return name
    .split(".")[0]
    .split("-")
    .joinToString("") { part -> part.replaceFirstChar { it.titlecase(Locale.ROOT) } }
}

fun escapeName(name: String): String = if (name.contains('-')) "\"$name\"" else name

/**
 * Returns a union of string literals. An empty union is "never".
 */
fun joinOptionList(optionList: List<String>): String =
  if (optionList.isEmpty()) {
    "never"
  } else {
    optionList.distinct().joinToString(" | ") { "\"$it\"" }
  }

/**
 * Returns the relative import path from the directory of the file at [outputPath] to the generated form fragments.
 * Both paths are relative to the output directory.
 */
fun resolveFragmentsImportPath(outputPath: String): String {
  val fromDirectory = outputPath.split('/').dropLast(1)
  val fragmentsDirectory = listOf("cms", "form-fragments")
  val commonLength = fromDirectory.zip(fragmentsDirectory).takeWhile { (a, b) -> a == b }.size
  val relativePath =
    (List(fromDirectory.size - commonLength) { ".." } + fragmentsDirectory.drop(commonLength))
      .joinToString("/")

  return if (relativePath.startsWith(".")) relativePath else "./$relativePath"
}
