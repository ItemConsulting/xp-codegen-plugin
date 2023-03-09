package no.item.xp.plugin.parser

import arrow.core.Either
import no.item.xp.plugin.extensions.getChildNodesAtXPath
import no.item.xp.plugin.extensions.getChildNodesAtXPathAsEither
import no.item.xp.plugin.extensions.getNodeAttribute
import no.item.xp.plugin.models.*
import no.item.xp.plugin.util.*
import org.w3c.dom.Node
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

val XPATH_FACTORY: XPathFactory = XPathFactory.newInstance()
val xpathInputType: XPathExpression = XPATH_FACTORY.newXPath().compile("input[@type] | mixin | item-set | option-set | field-set")
val xpathOption: XPathExpression = XPATH_FACTORY.newXPath().compile("options/option[@name]")

fun parseInterfaceModel(node: Node, nameWithoutExtension: String, mixins: List<InterfaceModel>): Either<Throwable, InterfaceModel> {
  return parseFields(node, mixins)
    .map { InterfaceModel(nameWithoutExtension, it) }
}

fun parseFields(node: Node, mixins: List<InterfaceModel>): Either<Throwable, List<InterfaceModelField>> {
  return node
    .getChildNodesAtXPathAsEither(xpathInputType)
    .map { parseInputTypeList(it, mixins) }
}

fun parseInputTypeList(nodes: Collection<Node>, mixins: List<InterfaceModel>): List<InterfaceModelField> {
  return nodes
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
}

private fun findMixinFields(mixins: List<InterfaceModel>, node: Node): List<InterfaceModelField> {
  val mixinName = node.getNodeAttribute("name")
  return mixins.find { it.nameWithoutExtension == mixinName }?.fields ?: emptyList()
}

fun parseConfigOptionValue(inputNode: Node): List<String> {
  return inputNode
    .getChildNodesAtXPath(getXpathExpressionFromString("config/option/@value"))
    .mapNotNull { it.nodeValue }
}
