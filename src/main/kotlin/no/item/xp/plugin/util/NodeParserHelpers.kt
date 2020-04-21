package no.item.xp.plugin.util

import arrow.core.Option
import arrow.core.Some
import no.item.xp.plugin.extensions.getAttributeAsOption
import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.extensions.getChildNodesSequence
import no.item.xp.plugin.extensions.getTextContentAsOption
import no.item.xp.plugin.models.InputType
import no.item.xp.plugin.models.YesNoMaybe
import org.w3c.dom.Node

fun getTypeForGeneratedField(node: Node): Option<InputType> =
  node.getAttributeAsOption("type")
    .map{
      InputType.valueOf(it.toUpperCase())
    }

fun getCommentForGeneratedField(node: Node): Option<String> =
  node.getChildNodeAtXPath(getXpathExpressionFromString("label"))
  .flatMap(Node::getTextContentAsOption)

fun getNameForGeneratedField(node: Node ): Option<String> =
  node.getAttributeAsOption("name")

@Suppress("ReplaceWithEnumMap", "ReplacePutWithAssignment")
fun mapToHashMap(node: Node): Option<HashMap<YesNoMaybe, Boolean>> {
  val hashMap: HashMap<YesNoMaybe, Boolean> = HashMap()
  node.getChildNodesSequence().forEach {
    hashMap.put(
      when (it.textContent.toString().toUpperCase()) {
        "YES" -> YesNoMaybe.YES
        "NO" -> YesNoMaybe.NO
        else -> YesNoMaybe.MAYBE
      },
      it.getAttributeAsOption("value").fold(
        { false },
        { it1 -> it1 == it.textContent.toString() }
      )
    )
  }
  return Some(hashMap)
}
