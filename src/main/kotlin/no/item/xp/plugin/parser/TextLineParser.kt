package no.item.xp.plugin.parser

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.util.getCommentForGeneratedField
import no.item.xp.plugin.util.getNameForGeneratedField
import no.item.xp.plugin.util.isOptional
import org.w3c.dom.Node

fun parseTextLine(node: Node): Option<StringField> {
  return Option.applicative().mapN(
    getNameForGeneratedField(node),
    Some(isOptional(node)),
    Some(getCommentForGeneratedField(node))
  ) { (name: String, nullable: Boolean, comment: Option<String>) ->
    StringField(name, nullable, comment)
  }.fix()
}
