package no.item.xp.plugin

import no.item.xp.plugin.phrases.GenerateI18nPhrasesTask
import no.item.xp.plugin.util.getTargetFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import java.io.File

abstract class GenerateCodePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val javaExt = project.extensions.getByType(JavaPluginExtension::class.java)
    val files =
      javaExt.sourceSets
        .getAt("main")
        .resources
        .filter { it.extension == "xml" && it.name != "application.xml" && it.name != "styles.xml" }
        .files

    val targetDir = File(project.rootDir.absolutePath + File.separator + ".xp-codegen")

    project.tasks.register("generateTypeScript", GenerateCodeTask::class.java) {
      it.group = "xp"
      it.inputFiles.from(files)
      it.outputFiles.from(files.map { file -> getTargetFile(file, targetDir) })
      it.outputDir.fileValue(targetDir)
    }

    project.tasks.register("generateI18nPhrases", GenerateI18nPhrasesTask::class.java) {
      it.source(files)
      it.group = "xp"
      it.description = "Find all untranslated keys in XMl files and writes them into phrases.tmp.properties"
    }
  }
}
