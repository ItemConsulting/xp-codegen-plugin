@file:Suppress("UNUSED_PARAMETER")

package no.item.xp.plugin

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.list.foldable.find
import java.io.File
import java.lang.Exception
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import no.item.xp.plugin.models.XmlFile
import no.item.xp.plugin.models.XmlType
import no.item.xp.plugin.parser.parse
import no.item.xp.plugin.util.generateFilePathForInterface
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Document

open class GenerateTypeScriptTask @Inject constructor(private val extension: GenerateTypeScriptExtension) : DefaultTask() {

  private val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

  @TaskAction
    fun generateTypeScript() {
      val files: Sequence<String> = findXmlFiles()
      files.forEach { parseFile(it) }
  }
  private fun findXmlFiles(): Sequence<String> {
    return File(extension.fileDir)
      .walk()
      .filter { it.isFile }
      .map { it.absolutePath }
  }

  @Suppress("UNUSED_VARIABLE")
  private fun parseFile(filePath: String) {
    getXmlType(filePath)
      .map {
        val xmlFile: XmlFile = generateXmlFile(filePath, it)
        val document: Document = getXmlDocumentByFile(xmlFile)
        val interfaceName: String? = generateFilePathForInterface(File(filePath))
        val xml: Either<Throwable, Sequence<Option<Any>>> = parse(document)
        xml.fold(
          {throwable: Throwable -> handleError(throwable) },
          {sequence: Sequence<Option<Any>> -> handleSuccess(sequence)}
        )
      }
  }

  private fun getXmlType(filePath: String): Either<Throwable, XmlType> {
    val child: Path = Paths.get(filePath).toAbsolutePath()
    return returnIsChild(child)
  }

  private fun returnIsChild(child: Path): Either<Throwable, XmlType> =
    DIRECTORIES_AND_XMLTYPE
      .find { isChild(child, it.first) }
      .map { it.second }
      .toEither { Exception("could not find XmlType for $child") }

  private fun isChild(child: Path, parentText: String): Boolean {
    val parent: Path = Paths.get(parentText).toAbsolutePath()
    return child.startsWith(parent)
  }

  private fun generateXmlFile(filePath: String, xmlType: XmlType): XmlFile {
    return XmlFile(filePath, xmlType)
  }

  private fun getXmlDocumentByFile(xmlFile: XmlFile): Document =
    docBuilder.parse(File(xmlFile.path))

  @Suppress("SpellCheckingInspection")
  companion object {
    private val DIRECTORIES_AND_XMLTYPE: List<Pair<String, XmlType>> = listOf(
      Pair("../site/content-types", XmlType.CONTENT_TYPE),
      Pair("../site/layouts/", XmlType.LAYOUT),
      Pair("../site/mixins/", XmlType.MIXIN),
      Pair("../site/parts/", XmlType.PART),
      Pair("../site/pages/", XmlType.PAGE),
      Pair("../site/macros/", XmlType.MACRO),
      Pair("../idprovider/", XmlType.ID_PROVIDER),
      Pair("../site/", XmlType.SITE)
    )
  }

  private fun handleSuccess(sequence: Sequence<Option<Any>>) {
    //sende sequencen til writer'n
    //writern tar imot de forskjellige typene av GeneratedField og printer disse.
    TODO("Not yet implemented")
  }
  private fun handleError(throwable: Throwable) {
    //logge errors til console/fil.
    TODO("Not yet implemented")
  }
}
