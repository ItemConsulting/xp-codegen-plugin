package no.item.xp.codegen.form

import com.fasterxml.jackson.databind.JsonNode

/**
 * The items of a form in a YAML descriptor. Only the parts that affect the generated types are kept.
 */
sealed interface FormItem

data class Occurrences(
  val min: Int? = null,
  val max: Int? = null,
)

/**
 * An input. The configuration of the input type (e.g. "regexp" or "options") are properties of [properties].
 */
data class Input(
  val name: String?,
  val type: String?,
  val label: String?,
  val occurrences: Occurrences,
  val properties: JsonNode,
) : FormItem

data class FieldSet(
  val label: String?,
  val items: List<FormItem>,
) : FormItem

data class ItemSet(
  val name: String?,
  val label: String?,
  val occurrences: Occurrences,
  val items: List<FormItem>,
) : FormItem

data class OptionSet(
  val name: String?,
  val label: String?,
  val occurrences: Occurrences,
  val selection: Occurrences,
  val options: List<OptionSetOption>,
) : FormItem

data class OptionSetOption(
  val name: String?,
  val label: String?,
  val items: List<FormItem>,
)

data class FragmentReference(
  val name: String,
) : FormItem

/**
 * Returns the items in the "form" of a descriptor, or an empty list if it has no form
 */
fun readForm(descriptor: JsonNode): List<FormItem> = readFormItems(descriptor.get("form"))

fun readFormItems(node: JsonNode?): List<FormItem> = node?.filter { it.isObject }?.mapNotNull(::readFormItem).orEmpty()

fun readFormItem(node: JsonNode): FormItem? =
  if (node.has("include")) {
    node.textAt("include")?.let(::FragmentReference)
  } else {
    when (node.textAt("type")?.lowercase()) {
      "fieldset" ->
        FieldSet(readLocalizedText(node.get("label")), readFormItems(node.get("items")))
      "itemset" ->
        ItemSet(
          name = node.textAt("name"),
          label = readLocalizedText(node.get("label")),
          occurrences = readOccurrences(node.get("occurrences")),
          items = readFormItems(node.get("items")),
        )
      "optionset" ->
        OptionSet(
          name = node.textAt("name"),
          label = readLocalizedText(node.get("label")),
          occurrences = readOccurrences(node.get("occurrences")),
          selection = readOccurrences(node.get("selection")),
          options =
            node
              .get("options")
              ?.filter { it.isObject }
              ?.map(::readOptionSetOption)
              .orEmpty(),
        )
      "formfragment" ->
        node.textAt("name")?.let(::FragmentReference)
      else ->
        Input(
          name = node.textAt("name"),
          type = node.textAt("type"),
          label = readLocalizedText(node.get("label")),
          occurrences = readOccurrences(node.get("occurrences")),
          properties = node,
        )
    }
  }

private fun readOptionSetOption(node: JsonNode): OptionSetOption =
  OptionSetOption(
    name = node.textAt("name"),
    label = readLocalizedText(node.get("label")),
    items = readFormItems(node.get("items")),
  )

/**
 * A localized text is either a string, or an object with "text" and "i18n"
 */
fun readLocalizedText(node: JsonNode?): String? =
  when {
    node == null -> null
    node.isTextual -> node.asText()
    node.isObject -> node.textAt("text")
    else -> null
  }

fun readOccurrences(node: JsonNode?): Occurrences = Occurrences(node?.intAt("min"), node?.intAt("max"))

fun JsonNode.textAt(field: String): String? = get(field)?.takeIf { it.isTextual }?.asText()

fun JsonNode.intAt(field: String): Int? = get(field)?.takeIf { it.isIntegralNumber && it.canConvertToInt() }?.intValue()
