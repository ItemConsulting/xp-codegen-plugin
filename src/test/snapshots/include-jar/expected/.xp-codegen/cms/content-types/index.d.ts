// ⚠ Generated source files should not be edited. The changes will be lost when sources are regenerated.
export type Article = import("./article").Article;
export type JarArticle = import("./jar-article").JarArticle;

declare global {
  namespace XP {
    interface ContentTypes {
      "no.item.example:article": Article;
      "no.item.example:jar-article": JarArticle;
    }
  }
}
