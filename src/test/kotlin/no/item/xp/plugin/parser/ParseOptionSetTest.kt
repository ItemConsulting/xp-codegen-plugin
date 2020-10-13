package no.item.xp.plugin.parser

import no.item.xp.plugin.extensions.getChildNodeAtXPath
import no.item.xp.plugin.models.ObjectField
import no.item.xp.plugin.models.OptionSetField
import no.item.xp.plugin.models.StringField
import no.item.xp.plugin.stringToXMLDocument
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseOptionSetTest {
  @Test
  fun `parse OptionSet`() {
    // language=XML
    val xml =
      """
        <option-set name="myOptionSet">
          <occurrences minimum="1" maximum="1"/>
          <label>Select content manually?</label>
          
          <options minimum="1" maximum="1">
            <option name="no">
              <label>No</label>
              <default>true</default>
            </option>
    
            <option name="yes">
              <label>Yes</label>
              <items>
                <input name="articleList" type="ContentSelector">
                  <label>Select articles for the list</label>
                  <occurrences minimum="0" maximum="0"/>
                </input>
              </items>
            </option>
          </options>
        </option-set>
        """

    val result = parseOptionSet(stringToXMLDocument(xml).getChildNodeAtXPath("option-set")!!, emptyList())

    assertEquals(
      result,
      OptionSetField(
        "myOptionSet",
        "Select content manually?",
        false,
        false,
        listOf(
          ObjectField(
            "no",
            "No",
            true,
            false,
            listOf()
          ),
          ObjectField(
            "yes",
            "Yes",
            true,
            false,
            listOf(
              StringField(
                "articleList",
                "Select articles for the list",
                true,
                true
              )
            )
          )
        )
      )
    )
  }
}
