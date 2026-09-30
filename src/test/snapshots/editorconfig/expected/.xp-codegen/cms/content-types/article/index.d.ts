// ⚠ Generated source files should not be edited. The changes will be lost when sources are regenerated.
export type Article = {
	/**
	 * Title
	 */
	title: string;

	/**
	 * Author
	 */
	author?: {
		/**
		 * Name
		 */
		name?: string;
	};

	/**
	 * Layout
	 */
	layout?:
		| {
				/**
				 * Selected
				 */
				_selected: "wide";

				/**
				 * Wide
				 */
				wide: Record<string, unknown>;
			}
		| {
				/**
				 * Selected
				 */
				_selected: "columns";

				/**
				 * Columns
				 */
				columns: {
					/**
					 * Count
					 */
					count?: number;
				};
			};
};
