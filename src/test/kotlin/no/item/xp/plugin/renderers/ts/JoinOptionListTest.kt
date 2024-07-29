package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JoinOptionListTest {
  @Test
  fun `create a union if string literals`() {
    val result = joinOptionList(listOf("option1", "option2", "option3"))

    assertEquals(
      result,
      //language=TypeScript
      """"option1" | "option2" | "option3""""
    )
  }

  @Test
  fun `create a union of string literals with duplicates`() {
    val result = joinOptionList(listOf("option1", "option1", "option2", "option3"))

    assertEquals(
      result,
      //language=TypeScript
      """"option1" | "option2" | "option3""""
    )
  }
}
