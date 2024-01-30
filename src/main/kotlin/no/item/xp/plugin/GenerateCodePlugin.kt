package no.item.xp.plugin

import no.item.xp.plugin.translatelist.WriteUntranslatedPhrasesTask
import no.item.xp.plugin.util.getTargetFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import java.io.File

class GenerateCodePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val javaExt = project.extensions.getByType(JavaPluginExtension::class.java)
    val files =
      javaExt.sourceSets
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

    project.tasks.create("checkTranslation", WriteUntranslatedPhrasesTask::class.java).apply {
      source(files)
      group = "xp"
      description = "Find all untranslated resources in XMl files and write them into phrases.tmp.properties"
    }
  }
}
