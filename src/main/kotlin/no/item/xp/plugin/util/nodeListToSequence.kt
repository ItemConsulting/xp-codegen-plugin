package no.item.xp.plugin.util

import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun nodeListToSequence(nodeList: NodeList): Sequence<Node> =
  (0..nodeList.length)
    .map { nodeList.item(it) }
    .asSequence<Node>()
