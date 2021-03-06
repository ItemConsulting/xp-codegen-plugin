package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderInterfaceModelTest {
  @Test
  fun `render mixed`() {
    val result = renderInterfaceModelAsTypeScript(
      InterfaceModel(
        "my-type",
        listOf(
          StringField("name", "Name", false, false),
          StringField("emails", "Emails", true, true),
          BooleanField("isManager", "Is manager", false, false),
          NumberField("age", "Age", true, false),
          UnionOfStringLiteralField("favouriteColor", "Favourite color", false, false, listOf("red", "green", "blue"))
        )
      )
    )

    assertEquals(
      result,
      """
      #export interface MyType {
      #  /**
      #   * Name
      #   */
      #  name: string;
      #
      #  /**
      #   * Emails
      #   */
      #  emails?: Array<string>;
      #
      #  /**
      #   * Is manager
      #   */
      #  isManager: boolean;
      #
      #  /**
      #   * Age
      #   */
      #  age?: number;
      #
      #  /**
      #   * Favourite color
      #   */
      #  favouriteColor: "red" | "green" | "blue";
      #}
      #
      """.trimMargin("#")
    )
  }
}
