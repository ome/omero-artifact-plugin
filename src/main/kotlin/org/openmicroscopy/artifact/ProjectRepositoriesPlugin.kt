package org.openmicroscopy.artifact

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import java.net.URI

class ProjectRepositoriesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        repositories {
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