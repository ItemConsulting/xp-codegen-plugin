package no.item.xp.codegen.validation

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.Schema
import com.networknt.schema.SchemaRegistry
import com.networknt.schema.SpecificationVersion
import no.item.xp.codegen.SchemaViolation
import no.item.xp.codegen.descriptor.DescriptorKind
import java.util.concurrent.ConcurrentHashMap

/**
 * Validates descriptors against the JSON schemas from "com.enonic.xp:core-jsonschema", that are bundled in the plugin
 */
object SchemaValidator {
  private val registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12)
  private val schemas = ConcurrentHashMap<DescriptorKind, Schema>()

  fun validate(
    kind: DescriptorKind,
    source: String,
    descriptor: JsonNode,
  ): Either<SchemaViolation, JsonNode> {
    val errors = getSchema(kind).validate(descriptor)

    return if (errors.isEmpty()) {
      descriptor.right()
    } else {
      SchemaViolation(source, errors.map { it.toString() }.distinct()).left()
    }
  }

  private fun getSchema(kind: DescriptorKind): Schema =
    schemas.computeIfAbsent(kind) {
      val path = "/no/item/xp/codegen/schemas/${kind.schemaName}.json"
      val stream = checkNotNull(SchemaValidator::class.java.getResourceAsStream(path)) { "Missing JSON schema: $path" }
      stream.use { registry.getSchema(it) }
    }
}
