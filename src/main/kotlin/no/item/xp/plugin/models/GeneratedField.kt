package no.item.xp.plugin.models

import arrow.core.Option
import no.item.xp.plugin.writer.isNullable
import no.item.xp.plugin.writer.typeScriptType

data class GeneratedField(
  var name: String,
  var type: InputType,
  var nullable: Boolean,
  var subFields: Sequence<GeneratedField>,
  var comment: Option<String>
) {

  fun showGeneratedField(): String =
    returnCommentFromLabel().plus(returnStringValue()).plus("\n").plus("\n")

  private fun returnCommentFromLabel(): String =
    this.comment
    .filter { it.isNotBlank() }
    .fold(
      { "\n" },
      { "/** $it */\n" }
    )

  private fun returnStringValue(): String {
    val name: String = this.name
    val nullable: String = isNullable(this.nullable)
    val separator = ": "
    val type: String = typeScriptType(this.type)
    val end = ";"
    return "$name$nullable$separator$type$end"
  }
}
