/* eslint-disable */
export type Article = {
  /**
   * Title
   */
  title: string;

  /**
   * Category
   */
  category?: 'news' | 'sports';

  /**
   * Hyphenated field
   */
  'my-hyphenated-field'?: string;
};
