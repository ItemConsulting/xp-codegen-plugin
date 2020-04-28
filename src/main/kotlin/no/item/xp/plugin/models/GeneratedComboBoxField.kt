package no.item.xp.plugin.models

import arrow.core.Option
import no.item.xp.plugin.writer.isNullable

data class GeneratedComboBoxField(
  var name: String,
  var type: InputType,
  var nullable: Boolean,
  var optionList: Option<HashMap<YesNoMaybe, Boolean>>,
  var comment: Option<String>
) {
  fun showGeneratedComboBox(): String =
    getCommentFromLabel().plus(getStringValue()).plus("\n").plus("\n")

  private fun getCommentFromLabel(): String =
    this.comment
      .filter { it.isNotBlank() }
      .fold(
        { "\n" },
        { "/** $it */\n" }
      )

  private fun getStringValue(): String {
    val name: String = this.name
    val nullable: String = isNullable(this.nullable)
    val separator = ": "
    val options: String = getOptionList(this.optionList)
    val end = ";"
    return "$name$nullable$separator$options$end"
  }

  private fun getOptionList(optionList: Option<HashMap<YesNoMaybe, Boolean>>): String {
   var returnString = ""
   var isFollowed = false
   optionList.fold(
     { returnString += "" },
     { it.forEach { (k: YesNoMaybe, v: Boolean) ->
       if (v) {
         returnString += if (isFollowed) {
           " | \"${k.value}\""
         } else {
           "\""+k+"\""
         }
         isFollowed = true
       }
     } }
   )
   return returnString
  }
}
