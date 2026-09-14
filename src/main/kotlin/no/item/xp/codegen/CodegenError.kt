package no.item.xp.codegen

sealed interface CodegenError {
  val message: String
}

data class InvalidYaml(
  val source: String,
  val cause: String,
) : CodegenError {
  override val message: String
    get() = "Can't read \"$source\": $cause"
}

data class SchemaViolation(
  val source: String,
  val errors: List<String>,
) : CodegenError {
  override val message: String
    get() = "\"$source\" is not a valid descriptor:\n${errors.joinToString("\n") { "  - $it" }}"
}

data class DuplicateFieldNames(
  val fieldNames: List<String>,
  val source: String? = null,
) : CodegenError {
  /**
   * Adds the file the duplicates was found in, unless it has already been added by a more specific caller
   */
  fun withSource(source: String): DuplicateFieldNames = if (this.source == null) copy(source = source) else this

  override val message: String
    get() {
      val label = if (fieldNames.size == 1) "Duplicate field name" else "Duplicate field names"
      val names = fieldNames.joinToString(", ") { "\"$it\"" }
      val location = source?.let { " in \"$it\"" } ?: ""

      return "$label $names$location. A field name can only be used once in the same object."
    }
}

data class CyclicFormFragments(
  val names: List<String>,
) : CodegenError {
  override val message: String
    get() = "Form fragments have cyclic dependencies and cannot be resolved: ${names.joinToString(" -> ")}"
}
