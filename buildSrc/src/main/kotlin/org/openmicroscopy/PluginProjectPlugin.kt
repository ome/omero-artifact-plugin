package org.openmicroscopy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class PluginProjectPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        apply<AdditionalArtifactsPlugin>()
        apply<PluginPublishingPlugin>()
        apply<FunctionalTestPlugin>()
        apply<ReleasePlugin>()
    }
}