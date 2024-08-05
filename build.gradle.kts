import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish") version "1.2.1"
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "no.item.xp.plugin"
version = "2.6.1"

kotlin {
  jvmToolchain(11)
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("io.arrow-kt:arrow-core:1.2.4")
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
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
