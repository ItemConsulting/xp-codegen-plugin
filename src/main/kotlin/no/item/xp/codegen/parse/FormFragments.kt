package no.item.xp.codegen.parse

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
 * The form fragments that could be resolved in [fragments]. [failed] has the names of the form fragments that are
 * invalid, or include an invalid form fragment. [errors] only has the errors of the invalid form fragments themselves, so
 * every error is reported once.
 */
data class ResolvedFormFragments(
  val fragments: FormFragments,
  val failed: Set<String>,
  val errors: List<CodegenError>,
) {
  internal fun withFailure(
    name: String,
    error: CodegenError? = null,
  ) = copy(failed = failed + name, errors = errors + listOfNotNull(error))
}

/**
 * Parses all the form fragments. Form fragments can include other form fragments, so they are parsed after the form
 * fragments they depend on. References to missing form fragments are ignored. An invalid form fragment doesn't stop the
 * other form fragments from being resolved, so all the errors can be reported.
 */
fun resolveFormFragments(descriptors: List<FormFragmentDescriptor>): ResolvedFormFragments {
  val descriptorsByName = descriptors.associateBy { it.name }

  return descriptors.fold(ResolvedFormFragments(emptyMap(), emptySet(), emptyList())) { resolved, descriptor ->
    resolveFormFragment(descriptor, descriptorsByName, resolved, emptyList())
  }
}

private fun resolveFormFragment(
  descriptor: FormFragmentDescriptor,
  descriptorsByName: Map<String, FormFragmentDescriptor>,
  resolved: ResolvedFormFragments,
  visiting: List<String>,
): ResolvedFormFragments {
  if (descriptor.name in resolved.fragments || descriptor.name in resolved.failed) {
    return resolved
  }

  if (descriptor.name in visiting) {
    return resolved.withFailure(
      descriptor.name,
      CyclicFormFragments(visiting.dropWhile { it != descriptor.name } + descriptor.name),
    )
  }

  val dependencies =
    findFragmentReferences(descriptor.items)
      .distinct()
      .mapNotNull { descriptorsByName[it] }

  val resolvedWithDependencies =
    dependencies.fold(resolved) { acc, dependency ->
      resolveFormFragment(dependency, descriptorsByName, acc, visiting + descriptor.name)
    }

  // The error is already reported for the invalid dependency
  if (dependencies.any { it.name in resolvedWithDependencies.failed }) {
    return resolvedWithDependencies.withFailure(descriptor.name)
  }

  return parseTypeModel(descriptor.name, descriptor.items, resolvedWithDependencies.fragments).fold(
    { resolvedWithDependencies.withFailure(descriptor.name, it.withSource(descriptor.source)) },
    { resolvedWithDependencies.copy(fragments = resolvedWithDependencies.fragments + (descriptor.name to it)) },
  )
}
