package no.item.xp.plugin.util

// Xml-files in an Enonic XP application that are not descriptors with forms
val IGNORED_XML_FILE_NAMES = setOf("application.xml", "styles.xml")

// Directories in an Enonic XP application that contains descriptors
val XP_DESCRIPTOR_DIRECTORIES = listOf("site", "admin", "services", "tasks", "idprovider")

/**
 * Returns true if [entryName] (a path in a jar-file, using "/" as separator) is a descriptor that code should be
 * generated for
 */
fun isDescriptorInJar(entryName: String): Boolean {
  val fileName = entryName.substringAfterLast('/')

  return entryName.endsWith(".xml") &&
    fileName !in IGNORED_XML_FILE_NAMES &&
    XP_DESCRIPTOR_DIRECTORIES.any { entryName.startsWith("$it/") }
}
