package no.item.xp.codegen.parse

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import no.item.xp.codegen.DuplicateFieldNames
import no.item.xp.codegen.form.FieldSet
import no.item.xp.codegen.form.FormItem
import no.item.xp.codegen.form.FragmentReference
import no.item.xp.codegen.form.Input
import no.item.xp.codegen.form.ItemSet
import no.item.xp.codegen.form.Occurrences
import no.item.xp.codegen.form.OptionSet
import no.item.xp.codegen.model.Field
import no.item.xp.codegen.model.ObjectField
import no.item.xp.codegen.model.OptionSetField
import no.item.xp.codegen.model.TypeModel
import no.item.xp.codegen.model.UnknownField

/**
 * Resolved form fragments by name
 */
typealias FormFragments = Map<String, TypeModel>

fun parseTypeModel(
  name: String,
  items: List<FormItem>,
  fragments: FormFragments,
): Either<DuplicateFieldNames, TypeModel> = parseFields(items, fragments).map { TypeModel(name, it) }

fun parseFields(
  items: List<FormItem>,
  fragments: FormFragments,
): Either<DuplicateFieldNames, List<Field>> =
  either {
    val fields =
      items.flatMap { item ->
        when (item) {
          is Input -> listOfNotNull(parseInput(item))
          is ItemSet -> listOfNotNull(parseItemSet(item, fragments).bind())
          is OptionSet -> listOfNotNull(parseOptionSet(item, fragments).bind())
          is FieldSet -> parseFieldSet(item, fragments).bind()
          is FragmentReference -> fragments[item.name]?.fields.orEmpty()
        }
      }

    // Fields from form fragments and field sets are added to the same object, so their names can collide
    val duplicateFieldNames =
      fields
        .groupingBy { it.name }
        .eachCount()
        .filterValues { it > 1 }
        .keys
        .toList()

    ensure(duplicateFieldNames.isEmpty()) { DuplicateFieldNames(duplicateFieldNames) }

    fields
  }

fun parseUnknownField(
  name: String?,
  label: String?,
  occurrences: Occurrences,
): UnknownField? =
  name?.let {
    UnknownField(
      name = it,
      comment = label,
      isNullable = occurrences.min?.let { min -> min < 1 } ?: true,
      isArray = occurrences.max?.let { max -> max != 1 } ?: false,
    )
  }

fun parseFieldSet(
  fieldSet: FieldSet,
  fragments: FormFragments,
): Either<DuplicateFieldNames, List<Field>> = parseFields(fieldSet.items, fragments)

fun parseItemSet(
  itemSet: ItemSet,
  fragments: FormFragments,
): Either<DuplicateFieldNames, ObjectField?> =
  either {
    parseUnknownField(itemSet.name, itemSet.label, itemSet.occurrences)?.let { field ->
      ObjectField(field, parseFields(itemSet.items, fragments).bind(), findSingleFragmentName(itemSet.items, fragments))
    }
  }

fun parseOptionSet(
  optionSet: OptionSet,
  fragments: FormFragments,
): Either<DuplicateFieldNames, OptionSetField?> =
  either {
    parseUnknownField(optionSet.name, optionSet.label, optionSet.occurrences)?.let { field ->
      val options =
        optionSet.options.mapNotNull { option ->
          option.name?.let { name ->
            ObjectField(
              name = name,
              comment = option.label,
              isNullable = true,
              isArray = false,
              fields = parseFields(option.items, fragments).bind(),
              fragmentName = findSingleFragmentName(option.items, fragments),
            )
          }
        }

      OptionSetField(field, optionSet.selection.max?.let { it != 1 } ?: false, options)
    }
  }

/**
 * Returns the name of the form fragment if it is the only item in [items], and the form fragment exists
 */
fun findSingleFragmentName(
  items: List<FormItem>,
  fragments: FormFragments,
): String? = (items.singleOrNull() as? FragmentReference)?.name?.takeIf { it in fragments }

/**
 * Returns the names of all the form fragments that are referenced in [items], including nested items
 */
fun findFragmentReferences(items: List<FormItem>): List<String> =
  items.flatMap { item ->
    when (item) {
      is FragmentReference -> listOf(item.name)
      is FieldSet -> findFragmentReferences(item.items)
      is ItemSet -> findFragmentReferences(item.items)
      is OptionSet -> item.options.flatMap { findFragmentReferences(it.items) }
      is Input -> emptyList()
    }
  }
