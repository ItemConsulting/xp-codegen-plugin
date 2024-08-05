package no.item.xp.plugin.renderers.ts

import no.item.xp.plugin.models.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderOptionSetFieldTest {
  @Test
  fun `render option set field`() {
    val result =
      renderTypeModelAsTypeScript(
        ObjectTypeModel(
          "my-type",
          listOf(
            OptionSetField(
              "myOptionSet",
              "Select content manually?",
              false,
              false,
              false,
              listOf(
                ObjectField(
                  "no",
                  "No",
                  true,
                  false,
                  emptyList(),
                ),
                ObjectField(
                  "yes",
                  "Yes",
                  true,
                  false,
                  listOf(
                    StringField("articleList", "Select articles for the list", true, true),
                  ),
                ),
              ),
            ),
          ),
        ),
      )

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
      #""".trimMargin(
        //language=
        "#",
      ),
      result,
    )
  }

  @Test
  fun `render multiselect option set field`() {
    val result =
      renderTypeModelAsTypeScript(
        ObjectTypeModel(
          "my-type",
          listOf(
            OptionSetField(
              "myOptionSet",
              "Select content manually?",
              false,
              false,
              true,
              listOf(
                ObjectField(
                  "no",
                  "No",
                  true,
                  false,
                  emptyList(),
                ),
                ObjectField(
                  "yes",
                  "Yes",
                  true,
                  false,
                  listOf(
                    StringField("articleList", "Select articles for the list", true, true),
                  ),
                ),
              ),
            ),
          ),
        ),
      )

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
      #""".trimMargin(
        //language=
        "#",
      ),
      result,
    )
  }

  @Test
  fun `render Array of option set entries`() {
    val result =
      renderTypeModelAsTypeScript(
        ObjectTypeModel(
          "my-type",
          listOf(
            OptionSetField(
              "myOptionSet",
              "Select content manually?",
              true,
              true,
              false,
              listOf(
                ObjectField(
                  "no",
                  "No",
                  true,
                  false,
                  emptyList(),
                ),
                ObjectField(
                  "yes",
                  "Yes",
                  true,
                  false,
                  listOf(
                    StringField("articleList", "Select articles for the list", true, true),
                  ),
                ),
              ),
            ),
          ),
        ),
      )

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
      #""".trimMargin(
        //language=
        "#",
      ),
      result,
    )
  }
}
