# Enonic XP Codegen plugin

![build-test](https://github.com/ItemConsulting/xp-codegen-plugin/workflows/build-test/badge.svg?branch=main) [![PluginVersion](https://img.shields.io/maven-metadata/v.svg?label=gradle&metadataUrl=https://plugins.gradle.org/m2/no/item/xp/codegen/no.item.xp.codegen.gradle.plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/no.item.xp.codegen)

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
    id 'java'
    id 'no.item.xp.codegen' version '1.0.0'
}

jar {
    // Add this before your TypeScript build task
    dependsOn += generateTypeScript

    // If you want JSDoc generated instead (because JS-project), use this:
    // dependsOn += generateJSDoc

    // If you want io-ts codecs generated instead (must be used instead of generateTypeScript):
    // dependsOn += generateIoTs
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

### Validation using io-ts

The [io-ts](https://github.com/gcanti/io-ts) library provides a DSL for declaring the shape of an object, and use this
schema to validate that json matches that definition.

The *XP Codegen plugin* provides the option to generate *io-ts* types (+ TypeScript types) instead of normal TypeScript 
interfaces with the `generateIoTs` Gradle-task.

This lets developers verify that user inputted data is valid according to the XP Content Type
definition in the XML-file (it will even validate `input/config/regexp` and `input/config/max-length` for 
`<input type="TextLine">`). 

> Since we don't expect user inputs for all content types, we recommend adding `codegen-output="IoTs"` like this: `<form codegen-output="IoTs">`
> in your Content Type XMLs only where you get user input to be validated.

#### io-ts example

Let's add `codegen-output="IoTs"` to the `<form>` element form the previous example.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<content-type>
  <display-name>Article</display-name>
  <super-type>base:structured</super-type>

  <!-- We specify `codegen-output` to say we want io-ts definitions -->
  <form codegen-output="IoTs">
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

This will now generate the following TypeScript-file:

Note that it exports both a `const Article` and a `type Article`. These are in two different namespaces. The 
`type Article` will work as a drop in replacement for the `Article` interface generated in the previous example. 

```typescript
import * as t from 'io-ts';

export const Article = t.type({
  /**
   * Title of the article
   */
  title: t.string,

  /**
   * Main text body
   */
  body: t.union([t.undefined, t.string]),
});

export type Article = t.TypeOf<typeof Article>;
```

To use the io-ts `Article` type to validate the code we can do the following:

```typescript
import {Request, Response} from 'enonic-types/controller';
import {Article} from "../../content-types/article/article";
import {Either, isRight} from "fp-ts/Either";
import {Errors} from "io-ts";
import {getErrorDetailReporter} from "enonic-wizardry/reporters/ErrorDetailReporter";

const {create} = __non_webpack_require__('/lib/xp/content');
const {run} = __non_webpack_require__('/lib/xp/context');
const {sanitize} = __non_webpack_require__('/lib/xp/common');


export function post(req: Request): Response {
  const rawArticle: Partial<Article> = {
    title: emptyStringToUndefined(req.params.title),
    body: emptyStringToUndefined(req.params.body)
  };

  // `decode` is where io-ts is used to validate. It either returns:
  // - Errors on the left side
  // - The Article on the right side
  const decoded: Either<Errors, Article> = Article.decode(rawArticle);

  if(isRight(decoded)) {
    const article: Article = decoded.right;
    runAsSu(() => create({
        displayName: article.title,
        parentPath: req.params.parentPath!,
        contentType: `${app.name}:article`,
        name: sanitize(article.title),
        data: article
      })
    );

    return {
      status: 201,
      body: article
    };
  } else {
    return {
      status: 400,
      /**
       * If `title` was blank then `body` will become:
       * [
       *   {
       *     key: "title",
       *     message: "<i18n phrase with key: 'articleFormPart.error.title'>"
       *   }
       * ]
       */
      body: getErrorDetailReporter({ i18nPrefix: "articleFormPart.error" }).report(decoded),
    };
  }
}

const runContext = {
  user: {
    login: "su",
    idProvider: "system"
  },
  branch: 'draft'
};

function runAsSu(f: () => void): void {
  run(runContext, f);
}

function emptyStringToUndefined(str: string | undefined): string | undefined {
  return (str === undefined || str === null || str.length === 0) ? undefined : str;
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
