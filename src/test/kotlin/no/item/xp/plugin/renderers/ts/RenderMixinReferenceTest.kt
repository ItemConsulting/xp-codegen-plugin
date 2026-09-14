package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderMixinReferenceTest {
  private val mixinFields = listOf(StringField("url", "Url", false, false))

  private val options =
    listOf(
      ObjectField("none", "None", true, false, emptyList()),
      ObjectField("internal", "Internal", true, false, mixinFields, "link-internal"),
    )

  @Test
  fun `render item set with only a mixin`() {
    val result =
      renderTypeModelAsTypeScript(
        ObjectTypeModel(
          "my-type",
          listOf(
            ObjectField("links", "Links", true, true, mixinFields, "link"),
            ObjectField("mainLink", "Main link", false, false, mixinFields, "link"),
          ),
        ),
        "../../mixins",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Links
      #   */
      #  links?: Array<import("../../mixins/link").Link>;
      #
      #  /**
      #   * Main link
      #   */
      #  mainLink: import("../../mixins/link").Link;
      #};
      #""".trimMargin(
        //language=
        "#",
      ),
      result,
    )
  }

  @Test
  fun `render option set with an option with only a mixin`() {
    val result =
      renderTypeModelAsTypeScript(
        ObjectTypeModel(
          "my-type",
          listOf(
            OptionSetField("myLink", "Link", false, false, false, options),
          ),
        ),
        "../../mixins",
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
      #        internal: import("../../mixins/link-internal").LinkInternal;
      #      };
      #};
      #""".trimMargin(
        //language=
        "#",
      ),
      result,
    )
  }

  @Test
  fun `render multiselect option set with an option with only a mixin`() {
    val result =
      renderTypeModelAsTypeScript(
        ObjectTypeModel(
          "my-type",
          listOf(
            OptionSetField("myLinks", "Links", false, false, true, options),
          ),
        ),
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
      #""".trimMargin(
        //language=
        "#",
      ),
      result,
    )
  }
}
