package no.item.xp.plugin.models

import arrow.core.Option

data class GeneratedComboBoxField(
  var name: String,
  var type: InputType,
  var nullable: Boolean,
  var optionList: Option<HashMap<YesNoMaybe, Boolean>>,
  var comment: Option<String>
)