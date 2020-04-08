package no.item.xp.plugin.extensions

import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun NodeList.forEach(action: (Node) -> Unit) {
  (0 until this.length)
    .asSequence()
    .map { this.item(it) }
    .forEach { action(it) }
}

fun NamedNodeMap.forEach(action: (Node) -> Unit) {
  (0 until this.length)
    .asSequence()
    .map { this.item(it) }
    .forEach { action(it) }
}
