package no.item.xp.plugin.util

import org.gradle.api.artifacts.Configuration
import java.io.File
import java.util.jar.JarFile

fun getXmlFilesInJars(config: Configuration): List<XmlFileInJar> {
  return getDependencyJarFiles(config).flatMap { jarFile ->
    val zipFile = JarFile(jarFile)

    zipFile.entries().asSequence()
      .filter { jarEntry -> jarEntry.name.startsWith("site") && jarEntry.name.endsWith(".xml") }
      .map { XmlFileInJar(zipFile, it) }
  }
}

fun getDependencyJarFiles(config: Configuration): List<File> =
  if (config.isCanBeResolved) {
    config.files
      .filter { it.exists() }
      .distinctBy { it.name }
  } else {
    emptyList()
  }
