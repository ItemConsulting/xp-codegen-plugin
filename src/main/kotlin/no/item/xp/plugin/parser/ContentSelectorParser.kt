package no.item.xp.plugin.parser

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.sequence.foldable.isEmpty
import arrow.core.fix
import no.item.xp.plugin.models.GeneratedInputType
import no.item.xp.plugin.models.MultipleField
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.util.getCommentForGeneratedField
import no.item.xp.plugin.util.getConfigOptions
import no.item.xp.plugin.util.getNameForGeneratedField
import no.item.xp.plugin.util.isOptional
import org.w3c.dom.Node

fun parseContentSelector(node: Node): Option<GeneratedInputType> {
  return if (getConfigOptions(node).isEmpty())
    parseContentSelectorSingle(node)
  else
    parseContentSelectorMultiple(node)
}

fun parseContentSelectorSingle(node: Node): Option<StringField> =
  parseTextLine(node)

fun parseContentSelectorMultiple(node: Node): Option<MultipleField> {
  return Option.applicative().map(
    getNameForGeneratedField(node),
    Some(isOptional(node)),
    Some(getCommentForGeneratedField(node)),
    Some(getConfigOptions(node))
  ) { (name: String,
        nullable: Boolean,
        comment: Option<String>,
        configs: Sequence<String>
      ) ->
    MultipleField(name, nullable, comment, configs)
  }.fix()
}
