package no.item.xp.plugin.models

data class GeneratedField(
  var name: String,
  var type: InputType,
  var nullable: Boolean,
  var subFields: Sequence<GeneratedField>,
  var comment: String
)
