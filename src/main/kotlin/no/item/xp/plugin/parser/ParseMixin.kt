package no.item.xp.plugin.parser

import arrow.core.flatMap
import no.item.xp.plugin.CyclicDependenciesException
import no.item.xp.plugin.extensions.getChildNodesAtXPath
import no.item.xp.plugin.extensions.getFormNode
import no.item.xp.plugin.models.InterfaceModel
import no.item.xp.plugin.models.MixinDependencyModel
import no.item.xp.plugin.util.parseXml
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.w3c.dom.Node

val LOGGER: Logger = Logging.getLogger("GenerateTypeScript")

fun resolveMixinGraph(mixinFiles: FileCollection): List<InterfaceModel> {
  val mixinDependencies = mixinFiles
    .mapNotNull { file ->
      parseXml(file)
        .flatMap { doc -> doc.getFormNode() }
        .map { formNode -> parseMixinDependencyModel(formNode, file.nameWithoutExtension) }
        .getOrNull()
    }

  return mixinDependencies.mapNotNull { parseMixin(it, mixinDependencies) }
}

fun parseMixin(mixin: MixinDependencyModel, otherMixins: List<MixinDependencyModel>): InterfaceModel? {
  try {
    return walkMixinGraph(mixin, otherMixins)
  } catch (e: StackOverflowError) {
    throw CyclicDependenciesException("Mixins has cyclic dependencies and cannot be resolved")
  }
}

private fun walkMixinGraph(mixin: MixinDependencyModel, otherMixins: List<MixinDependencyModel>): InterfaceModel? {
  val dependentOnMixins = mixin.dependencies
    .mapNotNull { name ->
      val dependencyModel = otherMixins.find { it.name == name }

      if (dependencyModel == null) {
        LOGGER.warn("Missing mixin with name=\"${name}\" that mixin with name=\"${mixin.name}\" depends on")
      }

      dependencyModel
    }
    .mapNotNull {
      val interfaceModel = walkMixinGraph(it, otherMixins)

      if (interfaceModel == null) {
        LOGGER.warn("Mixin with name=\"${it.name}\" that mixin with name=\"${mixin.name}\" depends on can't be parsed")
      }

      interfaceModel
    }

  return parseInterfaceModel(mixin.node, mixin.name, dependentOnMixins).getOrNull()
}

fun parseMixinDependencyModel(formNode: Node, name: String): MixinDependencyModel {
  val dependencies = formNode
    .getChildNodesAtXPath("mixin/@name")
    .map { it.nodeValue }

  return MixinDependencyModel(name, dependencies, formNode)
}
