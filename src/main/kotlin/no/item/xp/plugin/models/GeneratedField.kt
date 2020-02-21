package no.item.xp.plugin.models

data class GeneratedField(
  val name: String,
  val type: InputType,
  val nullable: Boolean,
  val subfields: Sequence<GeneratedField>,
  val comment: String?
)
