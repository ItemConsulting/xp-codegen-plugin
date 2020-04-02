package no.item.xp.plugin.writer

import java.io.File
import no.item.xp.plugin.models.GeneratedField
import no.item.xp.plugin.models.XmlType

@Suppress("FunctionName")
fun TypeScriptWriter(interfaceName: String, type: XmlType, objList: List<GeneratedField>?): String {

  var content = ""
  if (objList != null) {
    for (fields: GeneratedField in objList) {
    }
  }
  File(interfaceName).bufferedWriter().use { out ->
    out.write(content)
  }
  return interfaceName
}
