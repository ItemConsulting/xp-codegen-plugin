package no.item.xp.plugin

import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class GenerateTypeScriptTask @Inject constructor(private val extension: GenerateTypeScriptExtension) : DefaultTask() {

    @TaskAction
    fun generateTypeScript() {
      val files = findXmlFiles()
      println("Found XML files: ")
      println("--------------------")
      files.forEach { println(it) }
      // 2.send dem til en parser
      // 3.send resultatet til en writer
      // 4.exit
    }

  private fun findXmlFiles(): Sequence<String> {
    return File(extension.fileDir)
      .walk()
      .filter { it.isFile && it.extension == "xml" }
      .map { it.absolutePath }
  }
}
