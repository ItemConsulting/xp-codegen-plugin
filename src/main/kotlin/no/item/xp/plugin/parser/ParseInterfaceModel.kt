package no.item.xp.plugin.parser

import arrow.core.Either
import no.item.xp.plugin.DuplicateFieldNameException
import no.item.xp.plugin.extensions.getChildNodesAtXPath
import no.item.xp.plugin.extensions.getChildNodesAtXPathAsEither
import no.item.xp.plugin.extensions.getNodeAttribute
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.models.ObjectTypeModelField
import no.item.xp.plugin.util.getXpathExpressionFromString
import org.w3c.dom.Node
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

val XPATH_FACTORY: XPathFactory = XPathFactory.newInstance()
val xpathInputType: XPathExpression = XPATH_FACTORY.newXPath().compile("input[@type] | mixin | item-set | option-set | field-set")
val xpathOption: XPathExpression = XPATH_FACTORY.newXPath().compile("options/option[@name]")

fun parseObjectTypeModel(
  node: Node,
  nameWithoutExtension: String,
  mixins: List<ObjectTypeModel>,
): Either<Throwable, ObjectTypeModel> {
  return parseFields(node, mixins)
    .map { ObjectTypeModel(nameWithoutExtension, it) }
}

fun parseFields(
  node: Node,
  mixins: List<ObjectTypeModel>,
): Either<Throwable, List<ObjectTypeModelField>> {
  return node
    .getChildNodesAtXPathAsEither(xpathInputType)
    .map { parseInputTypeList(it, mixins) }
}

fun parseInputTypeList(
  nodes: Collection<Node>,
  mixins: List<ObjectTypeModel>,
): List<ObjectTypeModelField> {
  val fields =
    nodes
      .flatMap { node ->
        when (node.nodeName) {
          "input" -> listOfNotNull(parseInput(node))
          "option-set" -> listOfNotNull(parseOptionSet(node, mixins))
          "item-set" -> listOfNotNull(parseItemSet(node, mixins))
          "field-set" -> parseFieldSet(node, mixins)
          "mixin" -> findMixinFields(mixins, node)
          else -> emptyList()
        }
      }

  // Fields from mixins and field-sets are added to the same object, so their names can collide
  val duplicateFieldNames =
    fields
      .groupingBy { it.name }
      .eachCount()
      .filterValues { it > 1 }
      .keys
      .toList()

  if (duplicateFieldNames.isNotEmpty()) {
    throw DuplicateFieldNameException(duplicateFieldNames)
  }

  return fields
}

private fun findMixinFields(
  mixins: List<ObjectTypeModel>,
  node: Node,
): List<ObjectTypeModelField> {
  val mixinName = node.getNodeAttribute("name")
  return mixins.find { it.nameWithoutExtension == mixinName }?.fields ?: emptyList()
}

/**
 * Returns the name of the mixin if it is the only form field in [itemsNode], and the mixin exists
 */
fun findSingleMixinName(
  itemsNode: Node,
  mixins: List<ObjectTypeModel>,
): String? {
  val mixinName =
    itemsNode
      .getChildNodesAtXPath(xpathInputType)
      .singleOrNull()
      ?.takeIf { it.nodeName == "mixin" }
      ?.getNodeAttribute("name")

  return mixinName?.takeIf { name -> mixins.any { it.nameWithoutExtension == name } }
}

fun parseConfigOptionValue(inputNode: Node): List<String> {
  return inputNode
    .getChildNodesAtXPath(getXpathExpressionFromString("config/option/@value"))
    .mapNotNull { it.nodeValue }
}
