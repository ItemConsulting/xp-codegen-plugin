# Enonic XP Codegen plugin

![build-test](https://github.com/ItemConsulting/xp-codegen-plugin/workflows/build-test/badge.svg?branch=main) [![PluginVersion](https://img.shields.io/maven-metadata/v.svg?label=gradle&metadataUrl=https://plugins.gradle.org/m2/no/item/xp/codegen/no.item.xp.codegen.gradle.plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/no.item.xp.codegen)

This is Gradle plugin for *Enonic XP 7 projects*. It requires at least **Gradle 8.7**.

The plugin parses the Enonic projects XML-files, and generates **TypeScript interfaces** that can be used in your 
server- or client-side code.

This creates a **tight coupling** between your configuration and your code. If you change an xml-file, the TypeScript
-files will be regenerated, and it will not compile until you have fixed your code.

This plugin can create interfaces for:

 - Content types
 - Pages
 - Parts
 - Site
 - Layout
 - Tasks
 - Macros
 - Id-provider
 - Mixins
 - X-data

The pluging includes in addition a feature to check that the xml elements marked with the attribute "i18n" have been translated in the file \src\main\resources\i18n\phrases.properties. Any element marked with "i18n" missing in the phrases.properties will be written in a temporory file in the same directory as phrases.properties named phrases.tmp.properties. The purpose is to make it easy for you to copy and paste entries from phrases.tmp.properties to phrases.properties and translate them. This feature is implemented as a gradle task called "checkTranslation" that is built-in this plugin.
 
 ## Usage

To get started add the following to your project's *build.gradle* file:  
 
 ```groovy
plugins {
    id 'java'
    id 'no.item.xp.codegen' version '2.7.0'
}

jar {
    // Add this before your TypeScript build task
    dependsOn += generateTypeScript
}

// Add dependency to webpack tasks too
task serverWebpack( type: NodeTask, dependsOn: [ npmInstall, generateTypeScript ] ) {
  ...
}
 ```

## Update *./tsconfig.json*

By setting the `rootDirs` field in *tsconfig.json*, you can "overlay" the two directory structures over each other, and
references to generated Types becomes very natural.

E.g if you have a content type in 
"**./resources/site/content-types/article/article.xml**", the generated TypeScript interface for that type can be imported
from "**./resources/site/content-types/article**" (or alternatively from "**./resources/site/content-types**").


```json
{
  "compilerOptions": {
    ...
      
    "rootDirs": [
      "./src/main/resources",
      "./.xp-codegen"
    ]
  },
  "include": [
    "./.xp-codegen/**/*",
    "./src/main/resources/**/*"
  ]
}
```

## Examples

### Generating TypeScript interfaces

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
// WARNING: This file was automatically generated by "no.item.xp.codegen". You may lose your changes if you edit it.
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

### Configuration options

The following configuration options can be used:
 
  * *singleQuote* (`boolean`) – If `true` all `"` in output will be replaced with `'`.
  * *prependText* (`string`) – Code to prepend to all the generated code files. By default this is the WARNING-text.

```groovy
jar {
    dependsOn += generateTypeScript {
      singleQuote = true
      prependText = "// This is a different message"
    }
}
```

> **Note**
> You can use `prependText` to give instructions to the e.g. the linter. If you add
`/* eslint-disable prettier/prettier */` you can stop [eslint](https://eslint.org/) from processing the generated files.

### Using the generated interfaces

Here we can see an example of using generated interfaces it in a part specified in 
**"./site/parts/article-view/article-view.xml"**.

```typescript
// We can import the generated Article interface from "./site/content-types/index.d.ts"
import type { Article } from "../../content-types";
// We can import the shape of the part config from "./index.d.ts"
import type { ArticleView } from ".";
// imports from XP libraries:
import { getContent, getComponent } from "/lib/xp/portal";
import { render } from "/lib/thymeleaf";
import type { PartComponent } from "@enonic-types/core";

const view = resolve("article-view.html");

type PartArticleView =  PartComponent<"com.myproject:article-view", ArticleView>;

export function get(): XP.Response {
  const content = getContent<Content<Article, "com.myproject:article">>();
  const part = getComponent<PartArticleView>();

  assertIsDefined(content);
  assertIsDefined(part);
  
  return {
    status: 200,
    body: render<ThymeleafParams>(view, {
      title: content.displayName,
      preface: content.data.preface,
      backgroundColor: part.config.backgroundColor
    }),
  };
}

interface ThymeleafParams {
  title: string;
  preface: string | undefined;
  backgroundColor: string;  
}

function assertIsDefined<T>(value: T): asserts value is NonNullable<T> {
  if (value === undefined || value === null) {
    throw new Error(`${value} is not defined`);
  }
}
```

### Generating phases to be translated
We can then run the `./gradlew checkTranslation` task, it will generate the file \src\main\resources\i18n\phrases.tmp.properties which contains the phrases that are missing from the phrases.properties. It is then easy to translate and then copy and past those entries in the phrases.properties. 


## Development

### Local manual testing

To test this plugin locally you can run the following task to publish the plugin locally on your machine.

```bash
./gradlew publishToMavenLocal
```

Then – in your Enonic-project – you can add the following to the top of your settings.gradle file to use the plugin:

 ```groovy
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
 ```

To use the plugin your can just run the following task:

```bash
./gradlew generateTypeScript
```

### Running tests

To run the unit tests, linting and plugin verification in the project you can run:

```bash
./gradlew test
./gradlew ktlintFormat
./gradlew validatePlugins
```

You should always run `./gradlew ktlintFormat` before committing code to git!

### Publishing to plugin portal

To publish to the plugin portal, you first need to set up your local api-keys. Instrunctions can be found in the 
[plugin documentation](https://plugins.gradle.org/docs/submit).

Then you can run the following to submit the plugin to the plugin portal:

```bash
./gradlew publishPlugins
```
