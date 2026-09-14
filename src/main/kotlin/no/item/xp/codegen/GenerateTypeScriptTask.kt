package no.item.xp.codegen

import arrow.core.getOrElse
import no.item.xp.codegen.descriptor.collectDescriptorSources
import no.item.xp.codegen.render.DEFAULT_INDENT_UNIT
import no.item.xp.codegen.render.IndentationResolver
import no.item.xp.codegen.render.applyOutputOptions
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

@DisableCachingByDefault(because = "Generating the types is faster than using the build cache")
abstract class GenerateTypeScriptTask : DefaultTask() {
  /**
   * The resource directories of the application, e.g. "src/main/resources"
   */
  @get:Internal
  abstract val resourceDirectories: ConfigurableFileCollection

  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:IgnoreEmptyDirectories
  val descriptorFiles: FileTree
    get() = resourceDirectories.asFileTree.matching { it.include("**/*.yaml", "**/*.yml") }

  /**
   * Jars with descriptors that types also are generated for, e.g. libraries in the "include" configuration
   */
  @get:Classpath
  abstract val includedJars: ConfigurableFileCollection

  /**
   * The ".editorconfig" files that decide the indentation of the generated files
   */
  @get:InputFiles
  @get:PathSensitive(PathSensitivity.ABSOLUTE)
  abstract val editorConfigFiles: ConfigurableFileCollection

  /**
   * If true, all double quotes in the generated files are replaced with single quotes
   */
  @get:Input
  abstract val singleQuote: Property<Boolean>

  /**
   * Text added to the top of every generated file
   */
  @get:Input
  abstract val prependText: Property<String>

  /**
   * If true, the parts, pages, layouts and mixins are added to the global interfaces "XpPartMap", "XpPageMap",
   * "XpLayoutMap" and "XpMixin"
   */
  @get:Input
  abstract val declareGlobals: Property<Boolean>

  /**
   * The name of the application, used in the keys of the global maps
   */
  @get:Input
  @get:Optional
  abstract val appName: Property<String>

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun generate() {
    val outputDir = outputDirectory.get().asFile
    val resourceFiles = mutableMapOf<String, File>()
    descriptorFiles.visit { details ->
      if (!details.isDirectory) {
        resourceFiles[details.relativePath.pathString] = details.file
      }
    }

    val sources = collectDescriptorSources(resourceFiles, includedJars.files)
    val settings = GenerationSettings(appName.orNull, declareGlobals.get())

    val generation =
      generate(sources, settings).fold(
        { errors -> throw GradleException(errors.joinToString("\n\n") { it.message }) },
        { it },
      )

    generation.warnings.forEach { logger.warn(it) }

    deleteGeneratedFiles(outputDir)

    val indentationResolver = IndentationResolver()

    generation.files.forEach { file ->
      val targetFile = File(outputDir, file.path)
      val indentUnit =
        indentationResolver.resolveIndentUnit(targetFile.toPath()).getOrElse {
          logger.warn("Can't read .editorconfig for ${targetFile.toURI()}, so the default indentation is used: ${it.message}")
          DEFAULT_INDENT_UNIT
        }
      val content = applyOutputOptions(file.render(indentUnit), prependText.get(), singleQuote.get())

      targetFile.parentFile.mkdirs()
      targetFile.writeText(content, Charsets.UTF_8)
      logger.info("Updated file: ${targetFile.toURI()}")
    }

    logger.lifecycle("Generated ${generation.files.size} TypeScript files in ${outputDir.toURI()}")
  }

  /**
   * Deletes the files from earlier runs, so types for removed descriptors are removed
   */
  private fun deleteGeneratedFiles(outputDir: File) {
    outputDir
      .walkBottomUp()
      .filter { it != outputDir }
      .forEach { file ->
        if (file.isFile && file.name.endsWith(".d.ts")) {
          file.delete()
        } else if (file.isDirectory && file.list().isNullOrEmpty()) {
          file.delete()
        }
      }
  }
}
