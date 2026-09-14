package no.item.xp.codegen.model

/**
 * The shape of the data of a descriptor, e.g. the "data" of a content type or the "config" of a part
 */
data class TypeModel(
  val name: String,
  val fields: List<Field>,
)

sealed interface Field {
  val name: String
  val comment: String?
  val isNullable: Boolean
  val isArray: Boolean
}

// name?: string
data class StringField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
) : Field {
  constructor(field: Field) : this(field.name, field.comment, field.isNullable, field.isArray)
}

// name?: string
data class StringFieldWithValidation(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val regexp: String?,
  val maxLength: Int?,
) : Field {
  constructor(field: Field, regexp: String?, maxLength: Int? = null) :
    this(field.name, field.comment, field.isNullable, field.isArray, regexp, maxLength)
}

// name?: number
data class NumberField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
) : Field {
  constructor(field: Field) : this(field.name, field.comment, field.isNullable, field.isArray)
}

// name?: number
data class NumberFieldWithValidation(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val min: Int?,
  val max: Int?,
) : Field {
  constructor(field: Field, min: Int?, max: Int?) : this(field.name, field.comment, field.isNullable, field.isArray, min, max)
}

// name?: "a" | "b" | "c"
data class UnionOfStringLiteralField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val optionList: List<String>,
) : Field {
  constructor(field: Field, optionList: List<String>) : this(field.name, field.comment, field.isNullable, field.isArray, optionList)
}

// name: boolean
data class BooleanField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
) : Field {
  constructor(field: Field) : this(field.name, field.comment, field.isNullable, field.isArray)
}

// name?: { ... }
data class ObjectField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val fields: List<Field>,
  // Name of the form fragment, if it is the only form item. The type will then be imported instead of inlined.
  val fragmentName: String? = null,
) : Field {
  constructor(field: Field, fields: List<Field>, fragmentName: String? = null) :
    this(field.name, field.comment, field.isNullable, field.isArray, fields, fragmentName)
}

// name?: unknown
data class UnknownField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
) : Field

data class OptionSetField(
  override val name: String,
  override val comment: String?,
  override val isNullable: Boolean,
  override val isArray: Boolean,
  val isMultiSelect: Boolean,
  val optionList: List<ObjectField>,
) : Field {
  constructor(field: Field, isMultiSelect: Boolean, optionList: List<ObjectField>) :
    this(field.name, field.comment, field.isNullable, field.isArray, isMultiSelect, optionList)
}
