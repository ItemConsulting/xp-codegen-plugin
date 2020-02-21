package no.item.xp.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class GenerateTypeScriptPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create(
            "generateTypeScript",
            GenerateTypeScriptExtension::class.java,
            target.objects
        )
        target.tasks.create(
            "generateTypeScript",
            GenerateTypeScriptTask::class.java,
            extension
        )
    }
}
