# Enonic XP Codegen plugin

![build-test](https://github.com/ItemConsulting/xp-codegen-plugin/workflows/build-test/badge.svg?branch=master) [![PluginVersion](https://img.shields.io/maven-metadata/v.svg?label=gradle&metadataUrl=https://plugins.gradle.org/m2/no/item/xp/codegen/no.item.xp.codegen.gradle.plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/no.item.xp.codegen)

This is Gradle plugin for *Enonic XP 7 projects*. It requires at least **Gradle 5.6**.

The plugin parses the Enonic projects XML-files, and generates **TypeScript interfaces** that can be used in your 
server- or client-side code.

The plugin can also output **JSDoc** instead of TypeScript, in case you are developing in JavaScript. 

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

To get started add the following to your project's *build.gradle* file:  
 
 ```groovy
plugins {
    id  'no.item.xp.codegen' version '1.0.0'
}

jar {
    // Add this before your TypeScript build task
    dependsOn += generateTypeScript

    // If you want JSDoc generated instead (because JS-project), use this:
    // dependsOn += generateJSDoc
}
 ```

## Example

Here is an example of how an xml-file can be parsed to create a TypeScript interface.

We have created a content type for `Article` in the file **content-types/article/article.xml**:

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

We can then run the `./gradlew generateTypeScript` task, which will generate a new file in 
**content-types/article/article.ts** with the following content:

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

### Local manual testing

To test this plugin locally you can run the following task to publish the plugin locally on your machine.

```bash
./gradlew publishToMavenLocal
```

Then – in your Enonic-project – you can add the following to your build.gradle file to use the plugin:

 ```groovy
 buildscript {
   repositories {
     mavenLocal()
   }
   dependencies {
     classpath "no.item.xp.plugin:xp-codegen-plugin:1.0.0"
   }
 }
 
 apply plugin: "no.item.xp.codegen"
 ```

To use the plugin your can just run the following task:

```bash
./gradlew generateTypeScript
```

### Running tests

To run the unit tests, linting and plugin verification in the project you can run:

```bash
./gradlew test
./gradlew klintFormat
./gradlew validatePlugins
```

You should always run `./gradlew klintFormat` before committing code to git!

### Publishing to plugin portal

To publish to the plugin portal, you first need to set up your local api-keys. Instrunctions can be found in the 
[plugin documentation](https://plugins.gradle.org/docs/submit).

Then you can run the following to submit the plugin to the plugin portal:

```bash
./gradlew publishPlugins
```
