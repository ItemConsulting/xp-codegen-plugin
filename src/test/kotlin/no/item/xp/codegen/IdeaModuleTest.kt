package no.item.xp.codegen

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertContains

private val BUILD_SCRIPT =
  """
  plugins {
    id "java"
    id "no.item.xp.codegen"
  }

  tasks.register("printIdeaModule") {
    def root = projectDir
    def sourceDirs = provider { idea.module.sourceDirs.collect { root.relativePath(it) }.sort() }
    def generatedSourceDirs = provider { idea.module.generatedSourceDirs.collect { root.relativePath(it) }.sort() }

    doLast {
      println "sourceDirs=" + sourceDirs.get()
      println "generatedSourceDirs=" + generatedSourceDirs.get()
    }
  }

  """.trimIndent() + "\n"

/**
 * IntelliJ IDEA warns against editing files in directories that the "idea" plugin reports as generated sources
 */
class IdeaModuleTest {
  @TempDir
  lateinit var projectDir: File

  @Test
  fun `mark the output directory as generated sources`() {
    val output = printIdeaModule()

    assertContains(output, "sourceDirs=[.xp-codegen, src/main/java]")
    assertContains(output, "generatedSourceDirs=[.xp-codegen]")
  }

  @Test
  fun `mark a custom output directory as generated sources`() {
    val output =
      printIdeaModule(
        """
        generateTypeScript {
          outputDirectory = layout.projectDirectory.dir("src/generated/types")
        }
        """.trimIndent(),
      )

    assertContains(output, "sourceDirs=[src/generated/types, src/main/java]")
    assertContains(output, "generatedSourceDirs=[src/generated/types]")
  }

  private fun printIdeaModule(buildScript: String = ""): String {
    File(projectDir, "settings.gradle").writeText("rootProject.name = \"idea-module\"\n")
    File(projectDir, "build.gradle").writeText(BUILD_SCRIPT + buildScript)

    return GradleRunner
      .create()
      .withProjectDir(projectDir)
      .withPluginClasspath()
      .withArguments("printIdeaModule", "--configuration-cache")
      .build()
      .output
  }
}
