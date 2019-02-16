package org.openmicroscopy

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin

class PluginHelper {

    companion object {
        fun getRuntimeClasspathConfiguration(project: Project): Configuration? =
                project.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
    }

}