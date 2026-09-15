# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A Gradle plugin (`no.item.xp.codegen`) that reads the YAML descriptors of an Enonic XP 8 application (content types,
form fragments, mixins, parts, pages, layouts, macros, `cms/cms.yaml`, tasks, id provider, admin tools/extensions,
APIs) and generates TypeScript `.d.ts` files in `.xp-codegen/` of the consuming project.

Version 3 only supports XP 8 (YAML). Version 2 supported XP 7 (XML); that code lives in git history under the old
`no.item.xp.plugin` package.

Requires JDK 25 and Gradle 9. The Kotlin `languageVersion`/`apiVersion` is pinned to 2.2 because Gradle 9.0 embeds
Kotlin 2.2, so don't use newer Kotlin standard library APIs.

## Commands

```bash
./gradlew check                          # tests + ktlint + validatePlugins (CI runs test, ktlintCheck, validatePlugins)
./gradlew test
./gradlew test --tests 'no.item.xp.codegen.parse.ResolveFormFragmentsTest'
./gradlew test --tests '*ResolveFormFragmentsTest.fail if form fragments*'   # single test (backtick names)
./gradlew ktlintFormat                   # run before committing
./gradlew validatePlugins
./gradlew test -PupdateSnapshots=true    # rewrite snapshot expected files, then review with git diff
./gradlew publishToMavenLocal            # try the plugin in a real XP project via mavenLocal()
```

## Architecture

The pipeline is split so that everything except file I/O at the edges is pure and testable without Gradle.

1. **`CodegenPlugin`** registers `generateTypeScript` when the `java` plugin is applied. Inputs are the `main`
   resources, jars in the `include` configuration and candidate `.editorconfig` files (project dir and its parents,
   plus `.xp-codegen/.editorconfig`). Output defaults to `.xp-codegen/`.
2. **`GenerateTypeScriptTask`** collects descriptor sources, calls `generate()`, fails the build with all error
   messages, deletes previously generated `.d.ts` files, then writes each file.
3. **`collectDescriptorSources`** (`descriptor/`) turns resource files and jar entries into `DescriptorSource`s.
   `DescriptorKind.of(path)` decides the kind from the path (`<dir>/<name>/<name>.yaml|yml`, like XP's
   `DescriptorKeyLocator`). A project descriptor overrides a jar descriptor with the same path; among jars the first
   one wins.
4. **`generate()`** (`Generator.kt`) does all the work:
   - read YAML (`yaml/`) → validate against the XP JSON schema for the kind (`validation/SchemaValidator`)
   - `readForm` → `form.FormItem`: a YAML-level model (Input, FieldSet, ItemSet, OptionSet, FragmentReference)
   - `resolveFormFragments` (`parse/FormFragments.kt`) resolves fragments in dependency order, detects cycles, and
     keeps going after errors so all of them are reported
   - `parseTypeModel` → `model.TypeModel` / `Field`: a TypeScript-level model (string, number, union, object, option
     set…). Input type → field mapping is in `parse/ParseInput.kt`. Macros go through `toMacroModel`, which makes
     every field a string.
   - render (`render/`): `renderTypeModel` for most kinds, `renderSiteConfig` (global `XP.SiteConfig`) for
     `cms/cms.yaml`, and index files per kind (`IndexRenderer`) with maps keyed by `appName`
5. Missing form fragments are ignored with a warning, not an error.

Other things that span several files:

- **Errors** use Arrow `Either` with the sealed `CodegenError`. Use `mapOrAccumulate` or other accumulation so one
  invalid descriptor doesn't hide errors in the others.
- **Indentation** is resolved per output file with ec4j, so `GeneratedFile.render` takes the indent unit as a
  parameter and renders lazily. Renderers write through `CodeWriter`. `singleQuote` and `prependText` are applied
  afterwards to the rendered string (`OutputOptions.kt`).
- **Form fragments** used as the only item of a set are rendered as `import("<relative path>/cms/form-fragments/<name>")`
  references instead of being inlined; the relative path comes from `resolveFragmentsImportPath`.
- **JSON schemas** are not in the repo. The `extractXpSchemas` build task pulls them from
  `com.enonic.xp:core-jsonschema:$xpVersion` (see `build.gradle.kts`) into `build/generated/xp-schemas` and bundles them
  as `no/item/xp/codegen/schemas/<kind>.json`. `DescriptorKind.schemaName` must match those file names. Bump
  `xpVersion` to use newer schemas.

## Tests

- Unit tests build form items from inline YAML with the helpers in `src/test/kotlin/no/item/xp/codegen/TestUtils.kt`.
- `GenerateCodeSnapshotTest` runs the real plugin with Gradle TestKit on `src/test/snapshots/<name>/input` and compares
  the output with `src/test/snapshots/<name>/expected`. If `<name>/jar` exists, it is packed into a jar in the
  `include` configuration. Expected files must stay byte-for-byte as generated (`.editorconfig` disables formatting
  there); change them only via `-PupdateSnapshots=true`.
