package no.item.xp.plugin.models

import java.io.Serializable

sealed class InterfaceModelField : Serializable {
  abstract val name: String
  abstract val comment: String?
  abstract val isNullable: Boolean
  abstract val isArray: Boolean
}

// name?: string
data class StringField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.isNullable, field.isArray)
}

// name?: string
data class StringFieldWithValidation(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val regexp: String?,
  val maxLength: Int?
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, regexp: String?) : this(field.name, field.comment, field.isNullable, field.isArray, regexp, null)
  constructor(field: InterfaceModelField, regexp: String?, maxLength: Int?) : this(field.name, field.comment, field.isNullable, field.isArray, regexp, maxLength)
}

// name?: number
data class NumberField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.isNullable, field.isArray)
}

// name?: number
data class NumberFieldWithValidation(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val min: Int?,
  val max: Int?
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, min: Int?, max: Int?) : this(field.name, field.comment, field.isNullable, field.isArray, min, max)
}

// name?: "a" | "b" | "c"
data class UnionOfStringLiteralField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val optionList: List<String>
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, optionList: List<String>) : this(field.name, field.comment, field.isNullable, field.isArray, optionList)
}

// name: boolean
data class BooleanField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.isNullable, field.isArray)
}

data class ObjectField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val fields: List<InterfaceModelField>
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, fields: List<InterfaceModelField>) : this(field.name, field.comment, field.isNullable, field.isArray, fields)
}

data class UnknownField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean
) : InterfaceModelField() {
  constructor(field: InterfaceModelField) : this(field.name, field.comment, field.isNullable, field.isArray)
}

data class OptionSetField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val optionList: List<ObjectField>
) : InterfaceModelField() {
  constructor(field: InterfaceModelField, optionList: List<ObjectField>) : this(field.name, field.comment, field.isNullable, field.isArray, optionList)
}
