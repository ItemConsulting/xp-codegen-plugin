package no.item.xp.plugin.models

import arrow.core.Option

data class GeneratedField(
  var name: String,
  var type: InputType,
  var nullable: Boolean,
  var subFields: Sequence<GeneratedField>,
  var comment: Option<String>
)
