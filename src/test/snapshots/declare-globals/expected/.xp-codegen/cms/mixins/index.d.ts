// ⚠ Generated source files should not be edited. The changes will be lost when sources are regenerated.
export type MenuItem = import("./menu-item").MenuItem;
export type Seo = import("./seo").Seo;

export type MixinMap = {
  "no-item-example"?: {
    "menu-item"?: MenuItem;
    seo?: Seo;
  };
};

declare global {
  interface XpMixin {
    "no-item-example"?: {
      "menu-item"?: MenuItem;
      seo?: Seo;
    };
  }
}
