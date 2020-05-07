package no.item.xp.plugin.models

import arrow.core.Option

sealed class GeneratedInputType {
  abstract val name: String
  abstract val comment: Option<String>
  abstract val nullable: Boolean
}

// name?: string
data class StringField(
  override val name: String,
  override var nullable: Boolean,
  override var comment: Option<String>
) : GeneratedInputType()

// name?: "a" | "b" | "c"
data class UnionOfStringField(
  override val name: String,
  override val nullable: Boolean,
  override val comment: Option<String>,
  val optionList: Sequence<String>
) : GeneratedInputType()
