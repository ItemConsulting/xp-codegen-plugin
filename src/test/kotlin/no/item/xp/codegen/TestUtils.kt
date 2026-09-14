package no.item.xp.codegen

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import no.item.xp.codegen.form.FieldSet
import no.item.xp.codegen.form.FormItem
import no.item.xp.codegen.form.Input
import no.item.xp.codegen.form.ItemSet
import no.item.xp.codegen.form.OptionSet
import no.item.xp.codegen.form.readForm
import no.item.xp.codegen.form.readFormItem

private val YAML_MAPPER = ObjectMapper(YAMLFactory())

fun yaml(text: String): JsonNode = YAML_MAPPER.readTree(text.trimIndent())

fun formItem(text: String): FormItem = requireNotNull(readFormItem(yaml(text)))

fun formItems(text: String): List<FormItem> = readForm(yaml(text))

fun input(text: String): Input = formItem(text) as Input

fun fieldSet(text: String): FieldSet = formItem(text) as FieldSet

fun itemSet(text: String): ItemSet = formItem(text) as ItemSet

fun optionSet(text: String): OptionSet = formItem(text) as OptionSet
