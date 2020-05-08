package no.item.xp.plugin

import arrow.core.Some
import java.io.BufferedReader
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList
import kotlin.test.Test
import kotlin.test.assertEquals
import no.item.xp.plugin.models.*
import no.item.xp.plugin.util.generateFilePathForInterface
import no.item.xp.plugin.writer.typeScriptWriter

class TypeScriptWriterTest {

  private val path: String = Paths.get("").toAbsolutePath().toString()

  @Test
  fun testOnCheckBox() {
    val objList: ArrayList<BooleanField> = arrayListOf()
    val generatedField = BooleanField(
      "gdprSigned",
      false,
      Some("GDPR Signed")
    )
    objList.add(generatedField)
    val type = XmlType.PART
    val file = File("src/test/testFiles/testCheckBox.xml")
    val generatedInterfaceFile: String = generateFilePathForInterface(file)
    val generatedTypeScriptFile: String = typeScriptWriter(generatedInterfaceFile, type, objList)
    val bufferedReader: BufferedReader = bufferedReader(generatedTypeScriptFile)
    val inputString: String = bufferedReader.use { it.readText() }
    val outputString: String =
      "export interface Part{\n" +
        "/**" +
        " GDPR Signed" +
        " */\n" +
        "gdprSigned: boolean;\n\n" +
        "}"
    assertEquals(inputString, outputString)
    Files.delete(Paths.get(generatedInterfaceFile))
  }

  @Test
  fun testOnContentSelector() {
    val objList: ArrayList<StringField> = arrayListOf()
    val generatedField = StringField(
      "fabTarget",
      true,
      Some("FAB button target")
    )
    objList.add(generatedField)
    val type = XmlType.CONTENT_TYPE
    val file = File("src/test/testFiles/testContentSelector.xml")
    val generatedInterfaceFile: String = generateFilePathForInterface(file)
    val generatedTypeScriptFile: String = typeScriptWriter(generatedInterfaceFile, type, objList)
    val bufferedReader: BufferedReader = bufferedReader(generatedTypeScriptFile)
    val inputString: String = bufferedReader.use { it.readText() }
    val outputString: String =
      "export interface ContentType{\n" +
        "/**" +
        " FAB button target" +
        " */\n" +
        "fabTarget?: string;\n\n" +
        "}"
    assertEquals(inputString, outputString)
    Files.delete(Paths.get(generatedInterfaceFile))
  }

  @Test
  fun testOnContentSelectorArray() {
    val objList: ArrayList<MultipleField> = arrayListOf()
    val generatedField = MultipleField(
      "members",
      true,
      Some("Members"),
      arrayOf<String>("employee", "some").asSequence()
    )
    objList.add(generatedField)
    val type = XmlType.CONTENT_TYPE
    val file = File("src/test/testFiles/testContentSelectorMultiple.xml")
    val generatedInterfaceFile: String = generateFilePathForInterface(file)
    val generatedTypeScriptFile: String = typeScriptWriter(generatedInterfaceFile, type, objList)
    val bufferedReader: BufferedReader = bufferedReader(generatedTypeScriptFile)
    val inputString: String = bufferedReader.use { it.readText() }
    val outputString: String =
      "export interface ContentType{\n" +
        "/**" +
        " Members" +
        " */\n" +
        "members?: Array<string>;\n\n" +
        "}"
    assertEquals(inputString, outputString)
    Files.delete(Paths.get(generatedInterfaceFile))
  }

  @Test
  fun testTypeScriptWriterOnComboBox() {
    val objList: ArrayList<UnionOfStringField> = arrayListOf()
    val unionOfStringField = UnionOfStringField(
      "invite",
      true,
      Some("Invited"),
      sequenceOf("Yes", "No", "Maybe")
    )
    objList.add(unionOfStringField)
    val type = XmlType.MIXIN
    val file = File("src/test/testFiles/testComboBox.xml")
    val generatedInterfaceFile: String = generateFilePathForInterface(file)
    val generatedTypeScriptFile: String = typeScriptWriter(generatedInterfaceFile, type, objList)
    val bufferedReader: BufferedReader = bufferedReader(generatedTypeScriptFile)
    val inputString: String = bufferedReader.use { it.readText() }
    val outputString: String =
      "export interface Mixin{\n" +
        "/**" +
        " Invited" +
        " */\n" +
        "invite?: \"Yes\" | \"No\" | \"Maybe\";" +
        "\n\n}"
    assertEquals(inputString, outputString)
    Files.delete(Paths.get(generatedInterfaceFile))
  }

  @Test
  fun testTypeScriptWriter() {
    val objList: ArrayList<StringField> = arrayListOf()
    val generatedField1 = StringField(
      "String1",
      false,
      Some("comment1")
    )
    val generatedField2 = StringField(
      "String2",
      true,
      Some("comment2")
    )
    objList.add(generatedField1)
    objList.add(generatedField2)

    val type = XmlType.MIXIN
    val file = File("src/test/testFiles/testMixin.xml")
    val generatedInterfaceFile: String = generateFilePathForInterface(file)

    assertEquals("\\src\\test\\testFiles\\testMixin.ts", generatedInterfaceFile.removePrefix(path))

    val generatedTypeScriptFile: String = typeScriptWriter(generatedInterfaceFile, type, objList)
    val bufferedReader: BufferedReader = bufferedReader(generatedTypeScriptFile)
    val inputString: String = bufferedReader.use { it.readText() }
    val outputString: String = "export interface Mixin{\n" +
      "/** comment1 */\n" +
      "String1: string;\n" +
      "\n" +
      "/** comment2 */\n" +
      "String2?: string;\n" +
      "\n" +
      "}"
    assertEquals(inputString, outputString)
    Files.delete(Paths.get(generatedInterfaceFile))
  }

  private fun bufferedReader(file: String): BufferedReader = File(file).bufferedReader()
}
