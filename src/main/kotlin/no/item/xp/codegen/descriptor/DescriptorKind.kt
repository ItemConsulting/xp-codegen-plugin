package no.item.xp.codegen.descriptor

/**
 * The kinds of descriptors in an Enonic XP 8 application that types are generated for
 */
enum class DescriptorKind(
  // Name of the JSON schema in "no/item/xp/codegen/schemas"
  val schemaName: String,
  private val pathPattern: Regex,
) {
  CONTENT_TYPE("content-type", namedDescriptor("cms/content-types")),
  FORM_FRAGMENT("form-fragment", namedDescriptor("cms/form-fragments")),
  MIXIN("mixin", namedDescriptor("cms/mixins")),
  PAGE("page", namedDescriptor("cms/pages")),
  PART("part", namedDescriptor("cms/parts")),
  LAYOUT("layout", namedDescriptor("cms/layouts")),
  MACRO("macro", namedDescriptor("cms/macros")),
  CMS("cms", singleDescriptor("cms/cms")),
  TASK("task", namedDescriptor("tasks")),
  ID_PROVIDER("idprovider", singleDescriptor("idprovider/idprovider")),
  ADMIN_TOOL("admin-tool", namedDescriptor("admin/tools")),
  ADMIN_EXTENSION("admin-extension", namedDescriptor("admin/extensions")),
  API("api", namedDescriptor("apis")),
  ;

  companion object {
    /**
     * Returns the kind of descriptor at [relativePath] (relative to the resources directory, using "/" as separator),
     * or null if it is not a descriptor that types are generated for
     */
    fun of(relativePath: String): DescriptorKind? = entries.firstOrNull { it.pathPattern.matches(relativePath) }
  }
}

// E.g. "cms/content-types/article/article.yaml", like DescriptorKeyLocator in XP
private fun namedDescriptor(directory: String) = Regex("^${Regex.escape(directory)}/([^/]+)/\\1\\.ya?ml$")

private fun singleDescriptor(pathWithoutExtension: String) = Regex("^${Regex.escape(pathWithoutExtension)}\\.ya?ml$")
