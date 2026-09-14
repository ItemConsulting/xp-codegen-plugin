package no.item.xp.codegen.render

import no.item.xp.codegen.model.ObjectField
import no.item.xp.codegen.model.OptionSetField
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.TypeModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderOptionSetFieldTest {
  private val options =
    listOf(
      ObjectField("no", "No", true, false, emptyList()),
      ObjectField("yes", "Yes", true, false, listOf(StringField("articleList", "Select articles for the list", true, true))),
    )

  private fun render(
    field: OptionSetField,
    indentUnit: String = DEFAULT_INDENT_UNIT,
  ) = renderTypeModel(TypeModel("my-type", listOf(field)), "../../form-fragments", indentUnit)

  @Test
  fun `render option set field`() {
    val result = render(OptionSetField("myOptionSet", "Select content manually?", false, false, false, options))

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Select content manually?
      #   */
      #  myOptionSet:
      #    | {
      #        /**
      #         * Selected
      #         */
      #        _selected: "no";
      #
      #        /**
      #         * No
      #         */
      #        no: Record<string, unknown>;
      #      }
      #    | {
      #        /**
      #         * Selected
      #         */
      #        _selected: "yes";
      #
      #        /**
      #         * Yes
      #         */
      #        yes: {
      #          /**
      #           * Select articles for the list
      #           */
      #          articleList?: Array<string> | string;
      #        };
      #      };
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render multiselect option set field`() {
    val result = render(OptionSetField("myOptionSet", "Select content manually?", false, false, true, options))

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Select content manually?
      #   */
      #  myOptionSet: {
      #    /**
      #     * Selected
      #     */
      #    _selected: Array<"no" | "yes">;
      #
      #    /**
      #     * No
      #     */
      #    no: Record<string, unknown>;
      #
      #    /**
      #     * Yes
      #     */
      #    yes: {
      #      /**
      #       * Select articles for the list
      #       */
      #      articleList?: Array<string> | string;
      #    };
      #  };
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render Array of option set entries`() {
    val result = render(OptionSetField("myOptionSet", "Select content manually?", true, true, false, options))

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Select content manually?
      #   */
      #  myOptionSet?: Array<
      #    | {
      #        /**
      #         * Selected
      #         */
      #        _selected: "no";
      #
      #        /**
      #         * No
      #         */
      #        no: Record<string, unknown>;
      #      }
      #    | {
      #        /**
      #         * Selected
      #         */
      #        _selected: "yes";
      #
      #        /**
      #         * Yes
      #         */
      #        yes: {
      #          /**
      #           * Select articles for the list
      #           */
      #          articleList?: Array<string> | string;
      #        };
      #      }
      #  >;
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render Array of multiselect option set entries`() {
    val result = render(OptionSetField("myOptionSet", null, true, true, true, options.take(1)))

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  myOptionSet?: Array<{
      #    /**
      #     * Selected
      #     */
      #    _selected: Array<"no">;
      #
      #    /**
      #     * No
      #     */
      #    no: Record<string, unknown>;
      #  }>;
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render option set field with tabs`() {
    val result = render(OptionSetField("myOptionSet", null, true, true, false, options.take(1)), "\t")

    assertEquals(
      "export type MyType = {\n" +
        "\tmyOptionSet?: Array<\n" +
        "\t\t| {\n" +
        "\t\t\t\t/**\n" +
        "\t\t\t\t * Selected\n" +
        "\t\t\t\t */\n" +
        "\t\t\t\t_selected: \"no\";\n" +
        "\n" +
        "\t\t\t\t/**\n" +
        "\t\t\t\t * No\n" +
        "\t\t\t\t */\n" +
        "\t\t\t\tno: Record<string, unknown>;\n" +
        "\t\t\t}\n" +
        "\t>;\n" +
        "};\n",
      result,
    )
  }
}
