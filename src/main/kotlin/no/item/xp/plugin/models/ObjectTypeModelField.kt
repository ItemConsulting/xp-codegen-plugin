package no.item.xp.plugin.models

import java.io.Serializable

sealed class ObjectTypeModelField : Serializable {
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
  override val isArray: Boolean,
) : ObjectTypeModelField() {
  constructor(field: ObjectTypeModelField) : this(field.name, field.comment, field.isNullable, field.isArray)
}

// name?: string
data class StringFieldWithValidation(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val regexp: String?,
  val maxLength: Int?,
) : ObjectTypeModelField() {
  constructor(field: ObjectTypeModelField, regexp: String?) : this(field.name, field.comment, field.isNullable, field.isArray, regexp, null)
  constructor(
    field: ObjectTypeModelField,
    regexp: String?,
    maxLength: Int?,
  ) : this(field.name, field.comment, field.isNullable, field.isArray, regexp, maxLength)
}

// name?: number
data class NumberField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
) : ObjectTypeModelField() {
  constructor(field: ObjectTypeModelField) : this(field.name, field.comment, field.isNullable, field.isArray)
}

// name?: number
data class NumberFieldWithValidation(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val min: Int?,
  val max: Int?,
) : ObjectTypeModelField() {
  constructor(
    field: ObjectTypeModelField,
    min: Int?,
    max: Int?,
  ) : this(field.name, field.comment, field.isNullable, field.isArray, min, max)
}

// name?: "a" | "b" | "c"
data class UnionOfStringLiteralField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val optionList: List<String>,
) : ObjectTypeModelField() {
  constructor(
    field: ObjectTypeModelField,
    optionList: List<String>,
  ) : this(field.name, field.comment, field.isNullable, field.isArray, optionList)
}

// name: boolean
data class BooleanField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
) : ObjectTypeModelField() {
  constructor(field: ObjectTypeModelField) : this(field.name, field.comment, field.isNullable, field.isArray)
}

data class ObjectField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val fields: List<ObjectTypeModelField>,
) : ObjectTypeModelField() {
  constructor(
    field: ObjectTypeModelField,
    fields: List<ObjectTypeModelField>,
  ) : this(field.name, field.comment, field.isNullable, field.isArray, fields)
}

data class UnknownField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
) : ObjectTypeModelField()

data class OptionSetField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val isMultiSelect: Boolean,
  val optionList: List<ObjectField>,
) : ObjectTypeModelField() {
  constructor(
    field: ObjectTypeModelField,
    isMultiSelect: Boolean,
    optionList: List<ObjectField>,
  ) : this(field.name, field.comment, field.isNullable, field.isArray, isMultiSelect, optionList)
}
