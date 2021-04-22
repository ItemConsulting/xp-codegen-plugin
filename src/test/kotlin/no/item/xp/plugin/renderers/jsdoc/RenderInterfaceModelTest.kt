package no.item.xp.plugin.renderers.jsdoc

import no.item.xp.plugin.models.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderInterfaceModelTest {
  @Test
  fun `render mixed`() {
    val result = renderInterfaceModelAsJSDoc(
      InterfaceModel(
        "my-type",
        listOf(
          StringField("name", "Name", false, false),
          StringField("emails", "Emails", true, true),
          BooleanField("isManager", "Is manager", false, false),
          NumberField("age", "Age", true, false),
          UnionOfStringLiteralField("favouriteColor", "Favourite color", false, false, listOf("red", "green", "blue")),
          ObjectField(
            "preferences",
            "Preferences",
            true,
            true,
            listOf(
              StringField("key", "Key", false, false),
              StringField("value", "Value", false, false)
            )
          )
        )
      )
    )

    assertEquals(
      """
      #/**
      # * @typedef {Object} MyType
      # * @property {string} name Name
      # * @property {string[]} [emails] Emails
      # * @property {boolean} isManager Is manager
      # * @property {number} [age] Age
      # * @property {("red" | "green" | "blue")} favouriteColor Favourite color
      # * @property {Object[]} [preferences] Preferences
      # * @property {string} preferences.key Key
      # * @property {string} preferences.value Value
      # */
      #""".trimMargin("#"),
      result
    )
  }
}
