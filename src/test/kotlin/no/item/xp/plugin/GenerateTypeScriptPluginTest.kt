package no.item.xp.plugin

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * A simple unit test for the 'no.item.xp.plugin.greeting' plugin.
 */
class GenerateTypeScriptPluginTest {
  @Test fun `plugin registers task`() {
    // Create a test project and apply the plugin
    val project = ProjectBuilder.builder().build()
    project.plugins.apply("no.item.xp.plugin.generateTypeScript")

    // Verify the result
    assertNotNull(project.tasks.findByName("generateTypeScript"))
  }
}
