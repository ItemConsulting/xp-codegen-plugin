package no.item.xp.plugin

import no.item.xp.plugin.util.FileType
import no.item.xp.plugin.util.getTargetFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class GenerateCodePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val javaExt = project.extensions
      .getByType(JavaPluginExtension::class.java)
    val files = javaExt.sourceSets.map { sourceSet ->
      sourceSet.resources.filter { it.extension == "xml" && it.name != "application.xml" }
    }.flatten()

    project.tasks.create("generateTypeScript", GenerateCodeTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(files.map { getTargetFile(it, FileType.TypeScript) })
      fileType = FileType.TypeScript
      group = "enonic"
    }

    project.tasks.create("generateTypeScriptDeclaration", GenerateCodeTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(files.map { getTargetFile(it, FileType.TypeScriptDeclaration) })
      fileType = FileType.TypeScriptDeclaration
      group = "enonic"
    }

    project.tasks.create("generateJSDoc", GenerateCodeTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(files.map { getTargetFile(it, FileType.JSDoc) })
      fileType = FileType.JSDoc
      group = "enonic"
    }

    project.tasks.create("generateIoTs", GenerateCodeTask::class.java).apply {
      inputFiles.from(files)
      outputFiles.from(files.map { getTargetFile(it, FileType.IoTs) })
      fileType = FileType.IoTs
      group = "enonic"
    }
  }
}
