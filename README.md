# Enonic XP TypeScript code generation plugin

Gradle plugin that generates TypeScript interfaces for Enonic XP.

## Usage:
 
In your projects *build.gradle*-file add:  

```groovy
apply plugin: "no.item.xp.plugin.generatetypescript"
...
buildscript {
    ...
    dependencies {
    ...
        classpath "no.item.xp.plugin:xp-ts-codegen-plugin:1.0.0"
    }
}
```

## Building

Build this plugin with:

```bash
--build-file ./<YOURPROJECT>/build.gradle
```  

and task: `generateTypeScript`
and before launch: `uploadArchives`

