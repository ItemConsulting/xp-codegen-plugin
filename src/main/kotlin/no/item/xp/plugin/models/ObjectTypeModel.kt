package no.item.xp.plugin.models

import java.io.Serializable

data class ObjectTypeModel(val nameWithoutExtension: String, val fields: List<ObjectTypeModelField>) : Serializable
