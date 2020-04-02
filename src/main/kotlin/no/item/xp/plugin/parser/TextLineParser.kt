package no.item.xp.plugin.parser

import arrow.core.*
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.*
import arrow.core.fix
import no.item.xp.plugin.models.GeneratedField
import no.item.xp.plugin.models.InputType
import no.item.xp.plugin.util.extractNodeValueFromAttribute
import no.item.xp.plugin.util.isOptional
import org.w3c.dom.Node

fun parseTextLine(node: Node): Option<GeneratedField> {
  val nameOpt: Option<String> = getNameForGeneratedField(node)
  val typeOpt: Option<InputType> = getTypeForGeneratedField(node)
  val nullableOpt: Option<Boolean> = isOptional(node)
  val subFieldsOpt: Option<Sequence<GeneratedField>> = Some(sequenceOf())
  val commentOpt: Option<String> = getCommentForGeneratedField(node)
  return Option.applicative().map(
    nameOpt,
    typeOpt,
    nullableOpt,
    subFieldsOpt,
    commentOpt
  ) { (name: String, type: InputType, nullable: Boolean, subFields: Sequence<GeneratedField>, comment: String) ->
    GeneratedField(name, type, nullable, subFields, comment)
  }.fix()
}

fun getNameForGeneratedField(node: Node): Option<String> = extractNodeValueFromAttribute(node, "name")

fun getTypeForGeneratedField(node: Node): Option<InputType> =
  extractNodeValueFromAttribute(node, "type")
    .fold(
      { none() },
      { Some(InputType.valueOf(it.toUpperCase())) }
    )

fun getCommentForGeneratedField(node: Node): Option<String> = Some(node.textContent)
