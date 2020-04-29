package no.item.xp.plugin.parser

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import no.item.xp.plugin.models.GeneratedField
import no.item.xp.plugin.models.InputType
import no.item.xp.plugin.util.getCommentForGeneratedField
import no.item.xp.plugin.util.getNameForGeneratedField
import no.item.xp.plugin.util.getTypeForGeneratedField
import no.item.xp.plugin.util.isOptional
import org.w3c.dom.Node

fun parseTextLine(node: Node): Option<GeneratedField> {
  return Option.applicative().map(
    getNameForGeneratedField(node),
    getTypeForGeneratedField(node),
    Some(isOptional(node)),
    Some(sequenceOf<GeneratedField>()),
    Some(getCommentForGeneratedField(node))
  ) { (name: String, type: InputType, nullable: Boolean, subFields: Sequence<GeneratedField>, comment: Option<String>) ->
    GeneratedField(name, type, nullable, subFields, comment)
  }.fix()
}

fun getTypeForGeneratedField(node: Node): Option<InputType> =
  node.getAttributeAsOption("type")
    .map {
      InputType.valueOf(it.toUpperCase())
    }

fun getCommentForGeneratedField(node: Node): Option<String> =
  node.getChildNodeAtXPath(getXpathExpressionFromString("label"))
    .flatMap(Node::getTextContentAsOption)
