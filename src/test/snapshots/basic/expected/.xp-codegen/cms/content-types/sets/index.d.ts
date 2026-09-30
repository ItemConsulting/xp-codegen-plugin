// ⚠ Generated source files should not be edited. The changes will be lost when sources are regenerated.
export type Sets = {
  /**
   * Field set title
   */
  fieldSetTitle: string;

  /**
   * Contact
   */
  contact: {
    /**
     * Email
     */
    email?: string;

    /**
     * Phone numbers
     */
    phoneNumbers?: Array<{
      /**
       * Number
       */
      number: string;
    }>;
  };

  /**
   * Empty item set
   */
  emptyItemSet?: Record<string, never>;

  /**
   * Single select
   */
  singleSelect:
    | {
        /**
         * Selected
         */
        _selected: "none";

        /**
         * None
         */
        none: Record<string, unknown>;
      }
    | {
        /**
         * Selected
         */
        _selected: "text";

        /**
         * Text
         */
        text: {
          /**
           * Text
           */
          text?: string;
        };
      };

  /**
   * Multi select
   */
  multiSelect?: {
    /**
     * Selected
     */
    _selected: Array<"first" | "second">;

    /**
     * First
     */
    first: Record<string, unknown>;

    /**
     * Second
     */
    second: {
      /**
       * Count
       */
      count?: number;
    };
  };

  /**
   * Option set array
   */
  optionSetArray?: Array<
    | {
        /**
         * Selected
         */
        _selected: "nested";

        /**
         * Nested
         */
        nested: {
          /**
           * Nested option set
           */
          nestedOptionSet?:
            | {
                /**
                 * Selected
                 */
                _selected: "a";

                /**
                 * A
                 */
                a: Record<string, unknown>;
              };
        };
      }
  >;
};
