package no.item.xp.plugin

import org.gradle.api.GradleException

class DuplicateFieldNameException(
  val fieldNames: List<String>,
  val source: String? = null,
) : GradleException(createMessage(fieldNames, source)) {
  /**
   * Adds the file the duplicates was found in, unless it has already been added by a more specific caller
   */
  fun withSource(source: String): DuplicateFieldNameException =
    if (this.source == null) DuplicateFieldNameException(fieldNames, source) else this
}

private fun createMessage(
  fieldNames: List<String>,
  source: String?,
): String {
  val label = if (fieldNames.size == 1) "Duplicate field name" else "Duplicate field names"
  val names = fieldNames.joinToString(", ") { "\"$it\"" }
  val location = source?.let { " in \"$it\"" } ?: ""

  return "$label $names$location. A field name can only be used once in the same object."
}
