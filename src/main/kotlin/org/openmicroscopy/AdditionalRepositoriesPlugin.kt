package org.openmicroscopy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories
import org.openmicroscopy.PluginHelper.Companion.createArtifactoryMavenRepo
import org.openmicroscopy.PluginHelper.Companion.createGitlabMavenRepo
import org.openmicroscopy.PluginHelper.Companion.createStandardMavenRepo
import org.openmicroscopy.PluginHelper.Companion.safeAdd
import java.net.URI

class AdditionalRepositoriesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        repositories {
            safeAdd(createArtifactoryMavenRepo())
            safeAdd(createGitlabMavenRepo())
            safeAdd(createStandardMavenRepo())

            maven {
                name = "ome.maven"
                url = URI.create("https://artifacts.openmicroscopy.org/artifactory/maven/")
            }
            maven {
                name = "unidata"
                url = URI.create("https://artifacts.unidata.ucar.edu/repository/unidata-all/")
            }
        }
    }
}