package no.item.xp.codegen.render

import no.item.xp.codegen.model.BooleanField
import no.item.xp.codegen.model.NumberField
import no.item.xp.codegen.model.ObjectField
import no.item.xp.codegen.model.OptionSetField
import no.item.xp.codegen.model.StringField
import no.item.xp.codegen.model.TypeModel
import no.item.xp.codegen.model.UnionOfStringLiteralField
import no.item.xp.codegen.model.UnknownField
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenderTypeModelTest {
  @Test
  fun `render mixed`() {
    val result =
      renderTypeModel(
        TypeModel(
          "my-type",
          listOf(
            StringField("name", "Name", false, false),
            StringField("emails", "Emails", true, true),
            BooleanField("isManager", "Is manager", false, false),
            NumberField("age", "Age", true, false),
            UnionOfStringLiteralField("favouriteColor", "Favourite color", false, false, listOf("red", "green", "blue")),
          ),
        ),
        "../../form-fragments",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  /**
      #   * Name
      #   */
      #  name: string;
      #
      #  /**
      #   * Emails
      #   */
      #  emails?: Array<string> | string;
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
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render fields without comments and with hyphens`() {
    val result =
      renderTypeModel(
        TypeModel(
          "my-type",
          listOf(
            StringField("withoutLabel", null, true, false),
            UnknownField("my-hyphenated-field", "  ", false, false),
          ),
        ),
        "../../form-fragments",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type MyType = {
      #  withoutLabel?: string;
      #
      #  "my-hyphenated-field": unknown;
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render empty types`() {
    val result =
      renderTypeModel(
        TypeModel(
          "2-columns",
          listOf(
            ObjectField("emptyItemSet", "Empty item set", true, false, emptyList()),
            ObjectField("emptyItemSets", null, false, true, emptyList()),
          ),
        ),
        "../../form-fragments",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type _2Columns = {
      #  /**
      #   * Empty item set
      #   */
      #  emptyItemSet?: Record<string, never>;
      #
      #  emptyItemSets: Array<Record<string, never>>;
      #};
      #""".trimMargin("#"),
      result,
    )

    assertEquals("export type NoForm = Record<string, never>;\n", renderTypeModel(TypeModel("no-form", emptyList()), ".."))
  }

  @Test
  fun `render nested item sets`() {
    val result =
      renderTypeModel(
        TypeModel(
          "sets",
          listOf(
            ObjectField(
              "contact",
              "Contact",
              false,
              false,
              listOf(
                StringField("email", "Email", true, false),
                ObjectField("phoneNumbers", "Phone numbers", true, true, listOf(StringField("number", "Number", false, false))),
              ),
            ),
          ),
        ),
        "../../form-fragments",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type Sets = {
      #  /**
      #   * Contact
      #   */
      #  contact: {
      #    /**
      #     * Email
      #     */
      #    email?: string;
      #
      #    /**
      #     * Phone numbers
      #     */
      #    phoneNumbers?: Array<{
      #      /**
      #       * Number
      #       */
      #      number: string;
      #    }>;
      #  };
      #};
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render site config`() {
    val result =
      renderSiteConfig(
        TypeModel(
          "cms",
          listOf(
            StringField("googleAnalyticsId", "Google Analytics id", true, false),
            OptionSetField(
              "footerLink",
              "Footer link",
              true,
              false,
              false,
              listOf(ObjectField("link", "Link", true, false, emptyList(), "link")),
            ),
          ),
        ),
        "./form-fragments",
      )

    assertEquals(
      //language=TypeScript
      """
      #export type SiteConfig = XP.SiteConfig;
      #
      #declare global {
      #  namespace XP {
      #    interface SiteConfig {
      #      /**
      #       * Google Analytics id
      #       */
      #      googleAnalyticsId?: string;
      #
      #      /**
      #       * Footer link
      #       */
      #      footerLink?:
      #        | {
      #            /**
      #             * Selected
      #             */
      #            _selected: "link";
      #
      #            /**
      #             * Link
      #             */
      #            link: import("./form-fragments/link").Link;
      #          };
      #    }
      #  }
      #}
      #""".trimMargin("#"),
      result,
    )
  }

  @Test
  fun `render empty site config`() {
    assertEquals(
      //language=TypeScript
      """
      #export type SiteConfig = XP.SiteConfig;
      #
      #declare global {
      #  namespace XP {
      #    interface SiteConfig {}
      #  }
      #}
      #""".trimMargin("#"),
      renderSiteConfig(TypeModel("cms", emptyList()), "./form-fragments"),
    )
  }

  @Test
  fun `render with tabs`() {
    val result =
      renderTypeModel(
        TypeModel("link", listOf(ObjectField("target", "Target", false, false, listOf(StringField("url", "Url", false, false))))),
        "..",
        "\t",
      )

    assertEquals(
      "export type Link = {\n" +
        "\t/**\n" +
        "\t * Target\n" +
        "\t */\n" +
        "\ttarget: {\n" +
        "\t\t/**\n" +
        "\t\t * Url\n" +
        "\t\t */\n" +
        "\t\turl: string;\n" +
        "\t};\n" +
        "};\n",
      result,
    )
  }
}
