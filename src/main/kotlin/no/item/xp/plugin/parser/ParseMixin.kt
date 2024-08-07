package no.item.xp.plugin.parser

import arrow.core.Either
import arrow.core.flatMap
import no.item.xp.plugin.CyclicDependenciesException
import no.item.xp.plugin.extensions.getChildNodesAtXPath
import no.item.xp.plugin.extensions.getFormNode
import no.item.xp.plugin.models.MixinDependencyModel
import no.item.xp.plugin.models.ObjectTypeModel
import no.item.xp.plugin.util.XmlFileInJar
import no.item.xp.plugin.util.parseXml
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.w3c.dom.Node
import java.io.File
import java.io.InputStream

val LOGGER: Logger = Logging.getLogger("GenerateTypeScript")

fun resolveMixinGraph(mixinFiles: List<Either<File, XmlFileInJar>>): List<ObjectTypeModel> {
  val mixinDependencies =
    mixinFiles
      .mapNotNull { entry ->
        parseXml(getInputStream(entry))
          .flatMap { doc -> doc.getFormNode() }
          .map { formNode ->
            parseMixinDependencyModel(formNode, getNameWithoutExtension(entry))
          }
          .getOrNull()
      }

  return mixinDependencies.mapNotNull { parseMixin(it, mixinDependencies) }
}

fun getNameWithoutExtension(entry: Either<File, XmlFileInJar>): String {
  return entry.fold(
    { left -> left.nameWithoutExtension },
    { right -> right.nameWithoutExtension },
  )
}

fun getInputStream(entry: Either<File, XmlFileInJar>): InputStream {
  return entry.fold(
    { left -> left.inputStream() },
    { right -> right.inputStream() },
  )
}

fun parseMixin(
  mixin: MixinDependencyModel,
  otherMixins: List<MixinDependencyModel>,
): ObjectTypeModel? {
  try {
    return walkMixinGraph(mixin, otherMixins)
  } catch (e: StackOverflowError) {
    throw CyclicDependenciesException("Mixins has cyclic dependencies and cannot be resolved")
  }
}

private fun walkMixinGraph(
  mixin: MixinDependencyModel,
  otherMixins: List<MixinDependencyModel>,
): ObjectTypeModel? {
  val dependentOnMixins =
    mixin.dependencies
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

  return parseObjectTypeModel(mixin.node, mixin.name, dependentOnMixins).getOrNull()
}

fun parseMixinDependencyModel(
  formNode: Node,
  name: String,
): MixinDependencyModel {
  val dependencies =
    formNode
      .getChildNodesAtXPath("//mixin/@name")
      .map { it.nodeValue }
      .distinct()

  return MixinDependencyModel(name, dependencies, formNode)
}
