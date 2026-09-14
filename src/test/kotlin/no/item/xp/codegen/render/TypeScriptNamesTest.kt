package no.item.xp.codegen.render

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TypeScriptNamesTest {
  @Test
  fun `create type name from file name`() {
    mapOf(
      "article" to "Article",
      "my-content-type" to "MyContentType",
      "2-columns" to "_2Columns",
      "article.yaml" to "Article",
      "alreadyCamelCase" to "AlreadyCamelCase",
    ).forEach { (name, expected) -> assertEquals(expected, getTypeName(name), name) }
  }

  @Test
  fun `escape names with hyphens`() {
    assertEquals("\"my-field\"", escapeName("my-field"))
    assertEquals("myField", escapeName("myField"))
  }

  @Test
  fun `create a union of string literals`() {
    assertEquals(""""option1" | "option2" | "option3"""", joinOptionList(listOf("option1", "option2", "option3")))
  }

  @Test
  fun `create a union of string literals with duplicates`() {
    assertEquals(""""option1" | "option2" | "option3"""", joinOptionList(listOf("option1", "option1", "option2", "option3")))
  }

  @Test
  fun `create an empty union`() {
    assertEquals("never", joinOptionList(emptyList()))
  }

  @Test
  fun `resolve form fragments import path`() {
    mapOf(
      "cms/content-types/article/index.d.ts" to "../../form-fragments",
      "cms/form-fragments/link/index.d.ts" to "..",
      "cms/index.d.ts" to "./form-fragments",
      "tasks/cleanup/index.d.ts" to "../../cms/form-fragments",
      "idprovider/index.d.ts" to "../cms/form-fragments",
    ).forEach { (path, expected) -> assertEquals(expected, resolveFragmentsImportPath(path), path) }
  }
}
