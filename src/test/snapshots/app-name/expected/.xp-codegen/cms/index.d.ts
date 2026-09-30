// ⚠ Generated source files should not be edited. The changes will be lost when sources are regenerated.
export type SiteConfig = XP.SiteConfig;

declare global {
  namespace XP {
    interface SiteConfig {
      /**
       * Google Analytics id
       */
      googleAnalyticsId?: string;

      /**
       * Footer link
       */
      footerLink?:
        | {
            /**
             * Selected
             */
            _selected: "link";

            /**
             * Link
             */
            link: import("./form-fragments").Link;
          };
    }
  }
}
