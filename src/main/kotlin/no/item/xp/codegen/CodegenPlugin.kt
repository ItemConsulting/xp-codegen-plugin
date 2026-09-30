package no.item.xp.codegen

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.io.File

const val DEFAULT_PREPEND_TEXT =
  "// ⚠ Generated source files should not be edited. The changes will be lost when sources are regenerated."

abstract class CodegenPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.withType(JavaPlugin::class.java) {
      val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
      val projectDirectory = project.layout.projectDirectory
      val outputDirectory = projectDirectory.dir(".xp-codegen")

      val generateTypeScript =
        project.tasks.register("generateTypeScript", GenerateTypeScriptTask::class.java) { task ->
          task.group = BasePlugin.BUILD_GROUP
          task.description = "Generates TypeScript types based on the YAML descriptors of the application"
          task.resourceDirectories.from(sourceSets.named("main").map { it.resources.sourceDirectories })
          task.includedJars.from(project.configurations.matching { it.name == "include" && it.isCanBeResolved })
          task.editorConfigFiles.from(
            project.provider { findEditorConfigFileCandidates(projectDirectory.asFile).filter { it.isFile } },
          )
          task.singleQuote.convention(false)
          task.prependText.convention(DEFAULT_PREPEND_TEXT)
          task.declareGlobals.convention(false)
          task.appName.convention(project.providers.gradleProperty("appName"))
          task.outputDirectory.convention(outputDirectory)
        }

      // IntelliJ IDEA only reads the module of the "idea" plugin when the plugin is applied
      project.pluginManager.apply(IdeaPlugin::class.java)
      // The output directory can be changed in the build script, so it is read after the project is evaluated
      project.afterEvaluate {
        val task = generateTypeScript.get()
        markAsGeneratedSources(project, task.outputDirectory.get().asFile)
      }
    }
  }
}

/**
 * Makes IntelliJ IDEA warn that the files in [directory] are generated, and shouldn't be edited
 */
private fun markAsGeneratedSources(
  project: Project,
  directory: File,
) {
  val module = project.extensions.getByType(IdeaModel::class.java).module

  // IDEA ignores generated source directories that aren't source directories too
  module.sourceDirs = module.sourceDirs + directory
  module.generatedSourceDirs = module.generatedSourceDirs + directory
}

/**
 * Returns the ".editorconfig" files that can affect the generated files
 */
private fun findEditorConfigFileCandidates(projectDirectory: File): List<File> =
  listOf(File(projectDirectory, ".xp-codegen/.editorconfig")) +
    generateSequence(projectDirectory.absoluteFile) { it.parentFile }.map { File(it, ".editorconfig") }
