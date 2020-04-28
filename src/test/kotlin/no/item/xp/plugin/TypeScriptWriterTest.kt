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
import no.item.xp.plugin.writer.TypeScriptWriter

class TypeScriptWriterTest {

  private val path: String = Paths.get("").toAbsolutePath().toString()

  @Test
  @Suppress("ReplaceWithEnumMap")
  fun testTypeScriptWriterOnComboBox() {
    val objList: ArrayList<GeneratedComboBoxField> = arrayListOf()
    val hashMap: HashMap<YesNoMaybe, Boolean> = HashMap()
    hashMap[YesNoMaybe.YES] = true
    hashMap[YesNoMaybe.NO] = true
    hashMap[YesNoMaybe.MAYBE] = true
    val generatedComboBoxField1 = GeneratedComboBoxField(
      "invite",
      InputType.COMBOBOX,
      true,
      Some(hashMap),
      Some("Invited")
    )
    objList.add(generatedComboBoxField1)

    val type = XmlType.MIXIN
    val file = File("src/test/testFiles/testComboBox.xml")
    val generatedInterfaceFile: String = generateFilePathForInterface(file)

    val generatedTypeScriptFile: String = TypeScriptWriter(generatedInterfaceFile, type, objList)
    val bufferedReader: BufferedReader = bufferedReader(generatedTypeScriptFile)
    val inputString: String = bufferedReader.use { it.readText() }
    val outputString: String =
      "export interface Mixin {" +
      "/**" +
      " * Invited" +
      " */" +
     " invite?: \"Yes\" | \"No\" | \"Maybe\";" +
     "}"
    assertEquals(inputString, outputString)
    Files.delete(Paths.get(generatedInterfaceFile))
  }

  @Test
  fun testTypeScriptWriter() {
    val objList: ArrayList<GeneratedField> = arrayListOf()
    val generatedField1 = GeneratedField(
      "String1",
      InputType.TEXTLINE,
      false,
      emptySequence(),
      Some("comment1")
    )
    val generatedField2 = GeneratedField(
      "String2",
      InputType.TEXTLINE,
      true,
      emptySequence(),
      Some("comment2")
    )
    objList.add(generatedField1)
    objList.add(generatedField2)

    val type = XmlType.MIXIN
    val file = File("src/test/testFiles/testMixin.xml")
    val generatedInterfaceFile: String = generateFilePathForInterface(file)

    assertEquals("\\src\\test\\testFiles\\testMixin.ts", generatedInterfaceFile.removePrefix(path))

    val generatedTypeScriptFile: String = TypeScriptWriter(generatedInterfaceFile, type, objList)
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
