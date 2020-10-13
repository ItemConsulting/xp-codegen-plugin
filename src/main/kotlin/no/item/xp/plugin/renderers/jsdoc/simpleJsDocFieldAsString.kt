package no.item.xp.plugin.renderers.jsdoc

import no.item.xp.plugin.models.InterfaceModelField
import no.item.xp.plugin.renderers.ts.escapeName

fun simpleJsDocFieldAsString(field: InterfaceModelField, innerType: String, path: List<String>): String {
  val comment = field.comment?.let { " $it" } ?: ""
  val name = when {
    path.isEmpty() -> escapeName(field.name)
    else -> path.joinToString(".", postfix = ".", transform = ::escapeName) + escapeName(field.name)
  }

  return " * @property {${innerType}${if (field.isArray) "[]" else ""}} ${indicateNullable(field, name)}$comment"
}

fun indicateNullable(field: InterfaceModelField, name: String): String {
  return if (field.isNullable) "[$name]" else name
}
