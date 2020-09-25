# Enonic XP Codegen plugin

This is Gradle plugin for *Enonic XP 7 projects*. 

It parses the projects XML-files, and generate TypeScript interfaces that can be used in your server- or client-side 
code.

This creates a **tight coupling** between your configuration and your code. If you change an xml-file, the TypeScript
-files will be regenerated, and it will not compile until you have fixed your code.

This plugin can create interfaces for:

 - Content types
 - Pages
 - Parts
 - Site
 - Layout
 - Macros
 - Id-provider
 - Mixins
 
 ## Usage
 
 To get started add the following to your projects *build.gradle*-file:  
 
 ```groovy
 buildscript {
   dependencies {
     classpath "no.item.xp.plugin:xp-ts-codegen-plugin:0.0.3-SNAPSHOT"
   }
 }
 
 apply plugin: "no.item.xp.plugin.generateTypeScript"
 ```

## Example

Given that we have a content-type described in **content-types/article/article.xml**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<content-type>
  <display-name>Article</display-name>
  <super-type>base:structured</super-type>
  <form>
    <input name="title" type="TextLine">
      <label>Title of the article</label>
      <occurrences minimum="1" maximum="1"/>
    </input>

    <input name="body" type="HtmlArea">
      <label>Main text body</label>
      <occurrences minimum="0" maximum="1"/>
    </input>
  </form>
</content-type>
```

Running *enonic-ts-codegen* will generate a TypeScript-interface in the file **content-types/article/article.ts**:

```typescript
export interface Article {
  /** 
   * Title of the article
   */
  title: string;
 
  /**
   * Main text body 
   */
  body?: string;
}
```

## Development

### Installing locally

To install this plugin locally for testing you can run:

```bash
./gradlew publishToMavenLocal
```

### Running tests

```bash
./gradlew test
```

