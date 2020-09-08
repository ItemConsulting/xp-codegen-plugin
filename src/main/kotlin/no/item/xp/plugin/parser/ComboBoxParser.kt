package no.item.xp.plugin.parser

import arrow.core.*
import arrow.core.extensions.option.applicative.applicative
import no.item.xp.plugin.models.UnionOfStringField
import no.item.xp.plugin.util.*
import org.w3c.dom.Node

fun parseComboBox(node: Node): Option<UnionOfStringField> {
  return Option.applicative().mapN(
    getNameForGeneratedField(node),
    Some(isOptional(node)),
    Some(getCommentForGeneratedField(node)),
    Some(getConfigOptions(node))
  ) { (
    name: String,
    nullable: Boolean,
    comment: Option<String>,
    configs: Sequence<String>
  ) ->
    UnionOfStringField(name, nullable, comment, configs)
  }.fix()
}
