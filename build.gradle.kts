import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val kotlinVersion: String = "2.4.20"

plugins {
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish") version "2.2.1"
  id("org.jetbrains.kotlin.jvm") version "2.4.20"
  id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "no.item.xp.plugin"
version = "2.8.0-SNAPSHOT"

kotlin {
  jvmToolchain(11)
  compilerOptions {
    // Gradle 8.13 embeds Kotlin 2.0.21, which can only read metadata up to 2.1
    languageVersion.set(KotlinVersion.KOTLIN_2_1)
    apiVersion.set(KotlinVersion.KOTLIN_2_0)
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  implementation("io.arrow-kt:arrow-core:2.2.3")
  testImplementation("org.junit.jupiter:junit-jupiter:5.14.4")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

gradlePlugin {
  website.set("https://item.no")
  vcsUrl.set("https://github.com/ItemConsulting/xp-codegen-plugin")
  plugins {
    create("codegen") {
      id = "no.item.xp.codegen"
      implementationClass = "no.item.xp.plugin.GenerateCodePlugin"
      displayName = "Enonic XP Code Generation"
      description = "Plugin for generating code based on XMLs in Enonic XP 7"
      tags.set(listOf("enonic", "xp", "codegen", "typescript", "javascript", "jsdoc"))
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

tasks.withType<Javadoc>().all {
  enabled = false
}

ktlint {
  debug.set(false)
  verbose.set(true)
  android.set(false)
  outputToConsole.set(true)
  outputColorName.set("RED")
  ignoreFailures.set(true)
  reporters {
    reporter(ReporterType.PLAIN)
    reporter(ReporterType.CHECKSTYLE)
  }
}
