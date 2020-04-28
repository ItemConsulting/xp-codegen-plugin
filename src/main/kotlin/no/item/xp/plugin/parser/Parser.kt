package no.item.xp.plugin.parser

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.either.monad.flatMap
import no.item.xp.plugin.extensions.applySequence
import no.item.xp.plugin.util.getFormElementChildren
import org.w3c.dom.Document
import org.w3c.dom.Node

fun parse(doc: Document): Either<Throwable, Sequence<Option<Any>>> =
  getFormElementChildren(doc)
    .flatMap { parseNodes(it) }

private fun parseNodes(nodes: Sequence<Node>): Either<Throwable, Sequence<Option<Any>>> =
  applySequence(
    nodes
      .map {
        createGeneratedField(it)
      }
  )

private fun createGeneratedField(node: Node): Either<Throwable, Option<Any>> =
  getInputType(node)
    .flatMap {
      when (it) {
        "textline" -> Either.right(parseTextLine(node))
        "combobox" -> Either.right(parseComboBox(node))
        else -> Either.left(Exception("Input type \"$it\" not found"))
      }
    }

private fun getInputType(node: Node): Either<Throwable, String> =
  Option
    .fromNullable(node.attributes.getNamedItem("type"))
    .toEither { Throwable("Could not parse input-type from node") }
    .flatMap {
      try {
        Either.right(it.nodeValue)
      } catch (e: Exception) {
        Either.left(e)
      }
    }
    .map { it.toString().toLowerCase() }
