package no.item.xp.plugin.extensions

import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun NodeList.toCollection(): Collection<Node> = (0..this.length).mapNotNull { this.item(it) }
