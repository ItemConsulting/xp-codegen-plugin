package no.item.xp.codegen.render

import no.item.xp.codegen.model.ObjectField
import no.item.xp.codegen.model.OptionSetField
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.TypeModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderFragmentReferenceTest {
  private val fragmentFields = listOf(StringField("url", "Url", false, false))

  private val options =
    listOf(
      ObjectField("none", "None", true, false, emptyList()),
      ObjectField("internal", "Internal", true, false, fragmentFields, "link-internal"),
    )

  @Test
  fun `render item set with only a form fragment`() {
    val result =
      renderTypeModel(
        TypeModel(
          "my-type",
          listOf(
            ObjectField("links", "Links", true, true, fragmentFields, "link"),
            ObjectField("mainLink", "Main link", false, false, fragmentFields, "link"),
          ),
        ),
        "../../form-fragments",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Links
      #   */
      #  links?: Array<import("../../form-fragments/link").Link>;
      #
      #  /**
      #   * Main link
      #   */
      #  mainLink: import("../../form-fragments/link").Link;
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render option set with an option with only a form fragment`() {
    val result =
      renderTypeModel(
        TypeModel("my-type", listOf(OptionSetField("myLink", "Link", false, false, false, options))),
        "../../form-fragments",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Link
      #   */
      #  myLink:
      #    | {
      #        /**
      #         * Selected
      #         */
      #        _selected: "none";
      #
      #        /**
      #         * None
      #         */
      #        none: Record<string, unknown>;
      #      }
      #    | {
      #        /**
      #         * Selected
      #         */
      #        _selected: "internal";
      #
      #        /**
      #         * Internal
      #         */
      #        internal: import("../../form-fragments/link-internal").LinkInternal;
      #      };
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render multiselect option set with an option with only a form fragment`() {
    val result =
      renderTypeModel(
        TypeModel("my-type", listOf(OptionSetField("myLinks", "Links", false, false, true, options))),
        "..",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Links
      #   */
      #  myLinks: {
      #    /**
      #     * Selected
      #     */
      #    _selected: Array<"none" | "internal">;
      #
      #    /**
      #     * None
      #     */
      #    none: Record<string, unknown>;
      #
      #    /**
      #     * Internal
      #     */
      #    internal: import("../link-internal").LinkInternal;
      #  };
      #};
      #""".trimMargin("#"),
      result,
    )
  }
}
