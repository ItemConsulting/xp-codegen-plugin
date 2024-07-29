package no.item.xp.plugin.models

import org.w3c.dom.Node

data class MixinDependencyModel(
  val name: String,
  val dependencies: List<String>,
  val node: Node,
)
