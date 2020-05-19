package no.item.xp.plugin.parser

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import no.item.xp.plugin.models.BooleanField
import no.item.xp.plugin.util.getCommentForGeneratedField
import no.item.xp.plugin.util.getNameForGeneratedField
import org.w3c.dom.Node

fun parseCheckBox(node: Node): Option<BooleanField> {
  return Option.applicative().map(
    getNameForGeneratedField(node),
    Some(false),
    Some(getCommentForGeneratedField(node))
  ) { (name: String, nullable: Boolean, comment: Option<String>) ->
    BooleanField(name, nullable, comment)
  }.fix()
}
