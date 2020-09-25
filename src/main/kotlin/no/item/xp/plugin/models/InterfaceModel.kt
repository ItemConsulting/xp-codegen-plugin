package no.item.xp.plugin.models

import java.io.Serializable

data class InterfaceModel(val nameWithoutExtension: String, val fields: List<InterfaceModelField>) : Serializable
