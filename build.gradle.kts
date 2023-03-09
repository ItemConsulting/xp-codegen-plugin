import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish") version "1.1.0"
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
}

group = "no.item.xp.plugin"
version = "2.2.2"

kotlin {
  jvmToolchain(11)
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("io.arrow-kt:arrow-core:1.1.5")
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

gradlePlugin {
  plugins {
    create("codegen") {
      id = "no.item.xp.codegen"
      implementationClass = "no.item.xp.plugin.GenerateCodePlugin"
      displayName = "Enonic XP Code Generation"
    }
  }
}

pluginBundle {
  website = "https://item.no"
  vcsUrl = "https://github.com/ItemConsulting/xp-codegen-plugin"
  description = "Plugin for generating code based on XMLs in Enonic XP 7"
  tags = listOf("enonic", "xp", "codegen", "typescript", "javascript", "jsdoc")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
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
