package no.item.xp.plugin

import no.item.xp.plugin.util.getTargetFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention

class GenerateCodePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val files = project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.map { sourceSet ->
      sourceSet.resources.filter { it.extension == "xml" && it.name != "application.xml" }
    }.flatten()

    val targetFiles = files.map(::getTargetFile)

    project.tasks.create("generateTypeScript", GenerateTypeScriptTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(targetFiles)
      group = "enonic"
    }
  }
}
