import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import javax.inject.Inject

plugins {
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish") version "2.2.1"
  id("org.jetbrains.kotlin.jvm") version "2.4.20"
  id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "no.item.xp.codegen"
version = "3.0.0-SNAPSHOT"

// The version of Enonic XP that the JSON schemas for the descriptors are taken from
val xpVersion = "8.0.4"

kotlin {
  jvmToolchain(25)
  compilerOptions {
    // Gradle 9.0 embeds Kotlin 2.2, and can only read metadata up to 2.3
    languageVersion.set(KotlinVersion.KOTLIN_2_2)
    apiVersion.set(KotlinVersion.KOTLIN_2_2)
  }
}

repositories {
  mavenCentral()
  maven("https://repo.enonic.com/public") {
    name = "Enonic"
    mavenContent { includeGroup("com.enonic.xp") }
  }
}

val xpSchemas = configurations.dependencyScope("xpSchemas")
val xpSchemasClasspath =
  configurations.resolvable("xpSchemasClasspath") {
    extendsFrom(xpSchemas.get())
    isTransitive = false
  }

dependencies {
  implementation(platform("com.fasterxml.jackson:jackson-bom:2.22.2"))
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.arrow-kt:arrow-core:2.2.3")
  // 3.x is built on Jackson 3, so the 2.x line is used to share Jackson 2 with the YAML parser
  implementation("com.networknt:json-schema-validator:2.0.7")
  implementation("org.ec4j.core:ec4j-core:1.2.0")

  xpSchemas("com.enonic.xp:core-jsonschema:$xpVersion")

  testImplementation(platform("org.junit:junit-bom:6.1.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Lets tasks read zip files without referencing the build script, which isn't supported by the configuration cache
interface InjectedArchiveOperations {
  @get:Inject
  val archiveOperations: ArchiveOperations
}

// The JSON schemas are bundled in the plugin, so the users of the plugin don't need the Enonic repository
val extractXpSchemas =
  tasks.register<Sync>("extractXpSchemas") {
    description = "Extracts the JSON schemas for XP descriptors from com.enonic.xp:core-jsonschema"
    val version = xpVersion
    val classpath = xpSchemasClasspath
    val archiveOperations = objects.newInstance<InjectedArchiveOperations>().archiveOperations
    from(classpath.map { files -> files.map { archiveOperations.zipTree(it) } }) {
      include("META-INF/schemas/$version/enonic-xp-*-$version.json")
      // "META-INF/schemas/8.0.4/enonic-xp-content-type-8.0.4.json" -> "no/item/xp/codegen/schemas/content-type.json"
      eachFile { path = "no/item/xp/codegen/schemas/${name.removePrefix("enonic-xp-").removeSuffix("-$version.json")}.json" }
    }
    includeEmptyDirs = false
    into(layout.buildDirectory.dir("generated/xp-schemas"))
  }

sourceSets.main {
  resources.srcDir(extractXpSchemas)
}

gradlePlugin {
  website.set("https://github.com/ItemConsulting/xp-codegen-plugin")
  vcsUrl.set("https://github.com/ItemConsulting/xp-codegen-plugin")
  plugins {
    create("codegen") {
      id = "no.item.xp.codegen"
      implementationClass = "no.item.xp.codegen.CodegenPlugin"
      displayName = "Enonic XP Code Generation"
      description = "Plugin for generating TypeScript types based on YAML descriptors in Enonic XP 8"
      tags.set(listOf("enonic", "xp", "codegen", "typescript"))
    }
  }
}

tasks.validatePlugins {
  enableStricterValidation.set(true)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()

  // Snapshot tests compare the generated code with the files in this directory
  val snapshotsDir = layout.projectDirectory.dir("src/test/snapshots")
  inputs.dir(snapshotsDir).withPropertyName("snapshots")
  systemProperty("snapshotsDir", snapshotsDir.asFile.absolutePath)
  // Run "./gradlew test -PupdateSnapshots=true" to replace the expected files with the current output
  systemProperty("updateSnapshots", providers.gradleProperty("updateSnapshots").getOrElse("false"))
}

ktlint {
  verbose.set(true)
  outputToConsole.set(true)
  outputColorName.set("RED")
  reporters {
    reporter(ReporterType.PLAIN)
    reporter(ReporterType.CHECKSTYLE)
  }
  filter {
    exclude { it.file.path.contains("/build/generated/") }
  }
}
