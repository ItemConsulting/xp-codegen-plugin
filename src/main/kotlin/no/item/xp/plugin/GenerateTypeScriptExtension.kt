package no.item.xp.plugin

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory

open class GenerateTypeScriptExtension @Inject constructor(objectFactory: ObjectFactory) {
    var fileDir = "./"
}
