package no.item.xp.codegen.parse

import no.item.xp.codegen.model.BooleanField
import no.item.xp.codegen.model.NumberField
import no.item.xp.codegen.model.NumberFieldWithValidation
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.StringFieldWithValidation
import no.item.xp.codegen.model.TypeModel
import no.item.xp.codegen.model.UnionOfStringLiteralField
import no.item.xp.codegen.model.UnknownField
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ToMacroModelTest {
  @Test
  fun `convert all fields to strings`() {
    val result =
      toMacroModel(
        TypeModel(
          "youtube",
          listOf(
            NumberField("width", "Width", true, false),
            NumberFieldWithValidation("height", "Height", false, false, 1, 1000),
            BooleanField("autoplay", "Autoplay", false, false),
            UnknownField("other", "Other", true, true),
            StringField("url", "Url", false, false),
            StringFieldWithValidation("start", "Start", true, false, "^\\d+$", null),
            UnionOfStringLiteralField("size", "Size", true, false, listOf("small", "large")),
          ),
        ),
      )

    assertEquals(
      TypeModel(
        "youtube",
        listOf(
          StringField("width", "Width", true, false),
          StringField("height", "Height", false, false),
          StringField("autoplay", "Autoplay", false, false),
          StringField("other", "Other", true, true),
          StringField("url", "Url", false, false),
          StringFieldWithValidation("start", "Start", true, false, "^\\d+$", null),
          UnionOfStringLiteralField("size", "Size", true, false, listOf("small", "large")),
        ),
      ),
      result,
    )
  }
}
