package no.item.xp.codegen.render

/**
 * Renders an index file that exports the types of all the descriptors in a directory
 */
fun renderIndex(
  names: List<String>,
  indentUnit: String = DEFAULT_INDENT_UNIT,
): String = CodeWriter(indentUnit).apply { exports(names) }.toString()

/**
 * Renders the index file for content types, which adds the content types to the global "XP.ContentTypes"
 */
fun renderContentTypeIndex(
  names: List<String>,
  appName: String?,
  indentUnit: String = DEFAULT_INDENT_UNIT,
): String =
  CodeWriter(indentUnit)
    .apply {
      exports(names)

      if (appName != null) {
        line()
        block("declare global {", "}") {
          block("namespace XP {", "}") {
            block("interface ContentTypes {", "}") {
              descriptorEntries(names, appName)
            }
          }
        }
      }
    }.toString()

/**
 * Renders the index file for parts, pages or layouts. The map from descriptor key to type is exported as [mapName], and
 * added to the global interface [globalInterfaceName] if [declareGlobals] is true.
 */
fun renderComponentIndex(
  names: List<String>,
  appName: String?,
  mapName: String,
  globalInterfaceName: String,
  declareGlobals: Boolean,
  indentUnit: String = DEFAULT_INDENT_UNIT,
): String =
  CodeWriter(indentUnit)
    .apply {
      exports(names)

      if (appName != null) {
        line()
        block("export type $mapName = {", "};") {
          descriptorEntries(names, appName)
        }

        if (declareGlobals) {
          line()
          block("declare global {", "}") {
            block("interface $globalInterfaceName {", "}") {
              descriptorEntries(names, appName)
            }
          }
        }
      }
    }.toString()

/**
 * Renders the index file for mixins. The mixins are grouped by application, like in "Content.x".
 */
fun renderMixinIndex(
  names: List<String>,
  appName: String?,
  declareGlobals: Boolean,
  indentUnit: String = DEFAULT_INDENT_UNIT,
): String =
  CodeWriter(indentUnit)
    .apply {
      exports(names)

      if (appName != null) {
        line()
        block("export type MixinMap = {", "};") {
          mixinEntries(names, appName)
        }

        if (declareGlobals) {
          line()
          block("declare global {", "}") {
            block("interface XpMixin {", "}") {
              mixinEntries(names, appName)
            }
          }
        }
      }
    }.toString()

private fun CodeWriter.exports(names: List<String>) {
  names.forEach { name ->
    line("export type ${getTypeName(name)} = import(\"./$name\").${getTypeName(name)};")
  }
}

private fun CodeWriter.descriptorEntries(
  names: List<String>,
  appName: String,
) {
  names.forEach { name -> line("\"$appName:$name\": ${getTypeName(name)};") }
}

private fun CodeWriter.mixinEntries(
  names: List<String>,
  appName: String,
) {
  block("\"${appName.replace('.', '-')}\"?: {", "};") {
    names.forEach { name -> line("${escapeName(name)}?: ${getTypeName(name)};") }
  }
}
