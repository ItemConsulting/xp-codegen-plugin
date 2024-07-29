package no.item.xp.plugin.util

import java.util.jar.JarFile
import java.util.zip.ZipEntry

data class XmlFileInJar(val jarFile: JarFile, val entry: ZipEntry)
