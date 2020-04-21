package no.item.xp.plugin.parser

import arrow.core.*
import arrow.core.extensions.option.applicative.applicative
import no.item.xp.plugin.models.GeneratedComboBoxField
import no.item.xp.plugin.models.InputType
import no.item.xp.plugin.models.YesNoMaybe
import no.item.xp.plugin.util.*
import org.w3c.dom.Node

fun parseComboBox(node: Node): Option<GeneratedComboBoxField> {
  return Option.applicative().map(
    getNameForGeneratedField(node),
    getTypeForGeneratedField(node),
    Some(isOptional(node)),
    Some(getConfigOptions(node)),
    Some(getCommentForGeneratedField(node))
  ) { (name: String, type: InputType, nullable: Boolean, configs: Option<HashMap<YesNoMaybe, Boolean>>, comment: Option<String>) ->
    GeneratedComboBoxField(name, type, nullable, configs, comment)
  }.fix()
}

