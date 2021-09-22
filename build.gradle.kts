import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish") version "0.12.0"
  id("org.jetbrains.kotlin.jvm") version "1.3.72"
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

group = "no.item.xp.plugin"
version = "1.1.9"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("io.arrow-kt:arrow-core:0.10.5")
  testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

gradlePlugin {
  plugins {
    create("codegen") {
      id = "no.item.xp.codegen"
      implementationClass = "no.item.xp.plugin.GenerateCodePlugin"
    }
  }
}

pluginBundle {
  website = "https://item.no"
  vcsUrl = "https://github.com/ItemConsulting/xp-codegen-plugin"
  description = "Plugin for generating code based on XMLs in Enonic XP 7"

  (plugins) {
    "codegen" {
      displayName = "Enonic XP Code Generation"
      tags = listOf("enonic", "xp", "codegen", "typescript", "javascript", "jsdoc")
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

ktlint {
  version.set("0.38.1")
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
