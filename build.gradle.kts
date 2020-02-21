import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  `java`
  `java-gradle-plugin`
  `maven-publish`
  id("org.jetbrains.kotlin.jvm") version "1.3.61"
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
  kotlin("kapt") version "1.3.61"
}

group = "no.item.xp.plugin"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

val arrowVersion = "0.10.4"

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("io.arrow-kt:arrow-core:$arrowVersion")
  implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
  kapt("io.arrow-kt:arrow-meta:$arrowVersion")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

gradlePlugin {
  val generateTypeScript by plugins.creating {
    id = "no.item.xp.plugin.generateTypeScript"
    implementationClass = "no.item.xp.plugin.GenerateTypeScriptPlugin"
  }
}

ktlint {
  version.set("0.36.0")
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

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifactId = "xp-ts-codegen-plugin"
      from(components["java"])
    }
  }
}
