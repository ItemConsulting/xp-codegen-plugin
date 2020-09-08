package no.item.xp.plugin

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class GenerateTypeScriptExtension @Inject constructor(@Suppress("UNUSED_PARAMETER") objectFactory: ObjectFactory) {
  var fileDir = "./"
}
