package no.item.xp.codegen.render

import arrow.core.Either
import org.ec4j.core.Resource
import org.ec4j.core.ResourcePropertiesService
import org.ec4j.core.model.Property
import java.nio.file.Path

/**
 * Finds the indentation to use in a generated file, based on the ".editorconfig" files that apply to the file
 */
class IndentationResolver {
  private val service = ResourcePropertiesService.default_()

  fun resolveIndentUnit(file: Path): Either<Throwable, String> =
    Either.catch {
      val properties = service.queryProperties(Resource.Resources.ofPath(file.toAbsolutePath(), Charsets.UTF_8))
      indentUnitOf(properties.properties)
    }
}

/**
 * Returns the string written once per indentation level, based on "indent_style", "indent_size" and "tab_width"
 */
fun indentUnitOf(properties: Map<String, Property>): String {
  fun valueOf(name: String): String? =
    properties[name]
      ?.takeUnless { it.isUnset }
      ?.sourceValue
      ?.trim()
      ?.lowercase()

  fun positiveIntOf(name: String): Int? = valueOf(name)?.toIntOrNull()?.takeIf { it > 0 }

  return if (valueOf("indent_style") == "tab") {
    "\t"
  } else {
    // "indent_size = tab" uses "tab_width"
    " ".repeat(positiveIntOf("indent_size") ?: positiveIntOf("tab_width") ?: DEFAULT_INDENT_UNIT.length)
  }
}
