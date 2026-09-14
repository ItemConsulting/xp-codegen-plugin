package no.item.xp.codegen.yaml

import arrow.core.Either
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.item.xp.codegen.InvalidYaml

private val YAML_MAPPER: ObjectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

/**
 * Parses [text] as YAML. [source] is used in the error message.
 */
fun readYaml(
  source: String,
  text: String,
): Either<InvalidYaml, JsonNode> =
  Either
    .catch { YAML_MAPPER.readTree(text) }
    .mapLeft { InvalidYaml(source, it.message ?: it.toString()) }
