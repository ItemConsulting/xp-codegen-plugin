package no.item.xp.plugin

import no.item.xp.plugin.util.FileType
import no.item.xp.plugin.util.getTargetFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention

class GenerateCodePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val files = project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.map { sourceSet ->
      sourceSet.resources.filter { it.extension == "xml" && it.name != "application.xml" }
    }.flatten()

    project.tasks.create("generateTypeScript", GenerateCodeTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(files.map { getTargetFile(it, FileType.TypeScript) })
      fileExtension = FileType.TypeScript.filePostfix
      group = "enonic"
    }

    project.tasks.create("generateJSDoc", GenerateCodeTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(files.map { getTargetFile(it, FileType.JSDoc) })
      fileExtension = FileType.JSDoc.filePostfix
      group = "enonic"
    }
  }
}
