package no.item.xp.plugin.models

import java.io.Serializable

sealed class InterfaceModelField : Serializable {
  abstract val name: String
  abstract val comment: String?
  abstract val nullable: Boolean
  abstract val isArray: Boolean
}

// name?: string
data class StringField(
  override val name: String,
  override val comment: String?,
  override val nullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.nullable, field.isArray)
}

// name?: number
data class NumberField(
  override val name: String,
  override val comment: String?,
  override val nullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.nullable, field.isArray)
}

// name?: "a" | "b" | "c"
data class UnionOfStringLiteralField(
  override val name: String,
  override val comment: String?,
  override val nullable: Boolean,
  override val isArray: Boolean,
  val optionList: List<String>
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, optionList: List<String>) : this(field.name, field.comment, field.nullable, field.isArray, optionList)
}

// name: boolean
data class BooleanField(
  override val name: String,
  override val comment: String?,
  override val nullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.nullable, field.isArray)
}

data class ObjectField(
  override val name: String,
  override val comment: String?,
  override val nullable: Boolean,
  override val isArray: Boolean,
  val fields: List<InterfaceModelField>
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, fields: List<InterfaceModelField>) : this(field.name, field.comment, field.nullable, field.isArray, fields)
}

data class UnknownField(
  override val name: String,
  override val comment: String?,
  override val nullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.nullable, field.isArray)
}

data class OptionSetField(
  override val name: String,
  override val comment: String?,
  override val nullable: Boolean,
  override val isArray: Boolean,
  val optionList: List<OptionSetFieldOption>
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, optionList: List<OptionSetFieldOption>) : this(field.name, field.comment, field.nullable, field.isArray, optionList)
}

data class OptionSetFieldOption(
  val name: String,
  val comment: String?,
  val fields: List<InterfaceModelField>
)
