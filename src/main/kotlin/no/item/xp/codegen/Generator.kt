package no.item.xp.codegen

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.mapOrAccumulate
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import no.item.xp.codegen.descriptor.DescriptorKind
import no.item.xp.codegen.descriptor.DescriptorSource
import no.item.xp.codegen.form.FormItem
import no.item.xp.codegen.form.readForm
import no.item.xp.codegen.parse.FormFragmentDescriptor
import no.item.xp.codegen.parse.FormFragments
import no.item.xp.codegen.parse.findFragmentReferences
import no.item.xp.codegen.parse.parseTypeModel
import no.item.xp.codegen.parse.resolveFormFragments
import no.item.xp.codegen.parse.toMacroModel
import no.item.xp.codegen.render.renderComponentIndex
import no.item.xp.codegen.render.renderContentTypeIndex
import no.item.xp.codegen.render.renderIndex
import no.item.xp.codegen.render.renderMixinIndex
import no.item.xp.codegen.render.renderSiteConfig
import no.item.xp.codegen.render.renderTypeModel
import no.item.xp.codegen.render.resolveFragmentsImportPath
import no.item.xp.codegen.validation.SchemaValidator
import no.item.xp.codegen.yaml.readYaml

data class GenerationSettings(
  val appName: String?,
  val declareGlobals: Boolean,
)

/**
 * A file to generate. [path] is relative to the output directory. [render] takes the indentation unit to use.
 */
class GeneratedFile(
  val path: String,
  val render: (indentUnit: String) -> String,
)

data class Generation(
  val files: List<GeneratedFile>,
  val warnings: List<String>,
)

private data class ParsedDescriptor(
  val source: DescriptorSource,
  val items: List<FormItem>,
)

/**
 * Parses and validates all the descriptors in [sources], and returns the TypeScript files to generate for them. All the
 * invalid descriptors are reported.
 */
fun generate(
  sources: List<DescriptorSource>,
  settings: GenerationSettings,
): Either<NonEmptyList<CodegenError>, Generation> =
  either {
    val descriptors =
      sources
        .mapOrAccumulate { source ->
          val text = Either.catch { source.readText() }.mapLeft { InvalidYaml(source.relativePath, it.toString()) }.bind()
          val node = readYaml(source.relativePath, text).bind()
          SchemaValidator.validate(source.kind, source.relativePath, node).bind()
          ParsedDescriptor(source, readForm(node))
        }.bind()

    val fragments =
      resolveFormFragments(
        descriptors
          .filter { it.source.kind == DescriptorKind.FORM_FRAGMENT }
          .map { FormFragmentDescriptor(it.source.name, it.source.relativePath, it.items) },
      ).mapLeft { nonEmptyListOf(it) }.bind()

    val typeFiles =
      descriptors
        .mapOrAccumulate { descriptor ->
          val model =
            parseTypeModel(descriptor.source.name, descriptor.items, fragments)
              .mapLeft { it.withSource(descriptor.source.relativePath) }
              .bind()
          val path = descriptor.source.relativePath.substringBeforeLast('/') + "/index.d.ts"
          val fragmentsImportPath = resolveFragmentsImportPath(path)

          when (descriptor.source.kind) {
            DescriptorKind.CMS -> GeneratedFile(path) { renderSiteConfig(model, fragmentsImportPath, it) }
            DescriptorKind.MACRO -> GeneratedFile(path) { renderTypeModel(toMacroModel(model), fragmentsImportPath, it) }
            else -> GeneratedFile(path) { renderTypeModel(model, fragmentsImportPath, it) }
          }
        }.bind()

    Generation(
      files = typeFiles + createIndexFiles(descriptors, settings),
      warnings = findMissingFragmentWarnings(descriptors, fragments),
    )
  }

private fun createIndexFiles(
  descriptors: List<ParsedDescriptor>,
  settings: GenerationSettings,
): List<GeneratedFile> {
  val (appName, declareGlobals) = settings

  fun namesOf(kind: DescriptorKind) =
    descriptors
      .filter { it.source.kind == kind }
      .map { it.source.name }
      .distinct()
      .sorted()

  return listOfNotNull(
    namesOf(DescriptorKind.CONTENT_TYPE).takeIf { it.isNotEmpty() }?.let { names ->
      GeneratedFile("cms/content-types/index.d.ts") { renderContentTypeIndex(names, appName, it) }
    },
    namesOf(DescriptorKind.PART).takeIf { it.isNotEmpty() }?.let { names ->
      GeneratedFile("cms/parts/index.d.ts") { renderComponentIndex(names, appName, "PartMap", "XpPartMap", declareGlobals, it) }
    },
    namesOf(DescriptorKind.LAYOUT).takeIf { it.isNotEmpty() }?.let { names ->
      GeneratedFile("cms/layouts/index.d.ts") { renderComponentIndex(names, appName, "LayoutMap", "XpLayoutMap", declareGlobals, it) }
    },
    namesOf(DescriptorKind.PAGE).takeIf { it.isNotEmpty() }?.let { names ->
      GeneratedFile("cms/pages/index.d.ts") { renderComponentIndex(names, appName, "PageMap", "XpPageMap", declareGlobals, it) }
    },
    namesOf(DescriptorKind.FORM_FRAGMENT).takeIf { it.isNotEmpty() }?.let { names ->
      GeneratedFile("cms/form-fragments/index.d.ts") { renderIndex(names, it) }
    },
    namesOf(DescriptorKind.MIXIN).takeIf { it.isNotEmpty() }?.let { names ->
      GeneratedFile("cms/mixins/index.d.ts") { renderMixinIndex(names, appName, declareGlobals, it) }
    },
  )
}

private fun findMissingFragmentWarnings(
  descriptors: List<ParsedDescriptor>,
  fragments: FormFragments,
): List<String> =
  descriptors.flatMap { descriptor ->
    findFragmentReferences(descriptor.items)
      .distinct()
      .filterNot { it in fragments }
      .map { "Missing form fragment \"$it\" is ignored in \"${descriptor.source.relativePath}\"" }
  }
