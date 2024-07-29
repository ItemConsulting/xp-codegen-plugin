package no.item.xp.plugin

import no.item.xp.plugin.util.getTargetFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import java.io.File

class GenerateCodePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val javaExt = project.extensions.getByType(JavaPluginExtension::class.java)
    val files = javaExt.sourceSets
      .getAt("main")
      .resources
      .filter { it.extension == "xml" && it.name != "application.xml" && it.name != "styles.xml" }
      .files

    val targetDir = File(project.rootDir.absolutePath + File.separator + ".xp-codegen")

    project.tasks.create("generateTypeScript", GenerateCodeTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(files.map { getTargetFile(it, targetDir) })
      outputDir.fileValue(targetDir)
      group = "xp"
    }
  }
}
