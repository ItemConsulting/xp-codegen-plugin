package no.item.xp.codegen.parse

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import no.item.xp.codegen.CodegenError
import no.item.xp.codegen.CyclicFormFragments
import no.item.xp.codegen.form.FormItem

data class FormFragmentDescriptor(
  val name: String,
  // Path of the descriptor, relative to the resources directory
  val source: String,
  val items: List<FormItem>,
)

/**
 * Parses all the form fragments. Form fragments can include other form fragments, so they are parsed after the form
 * fragments they depend on. References to missing form fragments are ignored.
 */
fun resolveFormFragments(descriptors: List<FormFragmentDescriptor>): Either<CodegenError, FormFragments> =
  either {
    val descriptorsByName = descriptors.associateBy { it.name }

    descriptors.fold(emptyMap()) { resolved, descriptor ->
      resolveFormFragment(descriptor, descriptorsByName, resolved, emptyList())
    }
  }

private fun Raise<CodegenError>.resolveFormFragment(
  descriptor: FormFragmentDescriptor,
  descriptorsByName: Map<String, FormFragmentDescriptor>,
  resolved: FormFragments,
  visiting: List<String>,
): FormFragments {
  if (descriptor.name in resolved) {
    return resolved
  }

  ensure(descriptor.name !in visiting) {
    CyclicFormFragments(visiting.dropWhile { it != descriptor.name } + descriptor.name)
  }

  val resolvedWithDependencies =
    findFragmentReferences(descriptor.items)
      .distinct()
      .mapNotNull { descriptorsByName[it] }
      .fold(resolved) { acc, dependency ->
        resolveFormFragment(dependency, descriptorsByName, acc, visiting + descriptor.name)
      }

  val model =
    parseTypeModel(descriptor.name, descriptor.items, resolvedWithDependencies)
      .mapLeft { it.withSource(descriptor.source) }
      .bind()

  return resolvedWithDependencies + (descriptor.name to model)
}
