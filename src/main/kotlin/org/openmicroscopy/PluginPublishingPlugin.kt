package org.openmicroscopy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withType
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.openmicroscopy.PluginHelper.Companion.licenseGnu2

class PluginPublishingPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyJavaGradlePlugin()

        afterEvaluate {
            configurePluginMaven()
        }
    }

    private
    fun Project.applyJavaGradlePlugin() {
        apply<MavenPublishPlugin>()
        apply<JavaGradlePluginPlugin>()
    }

    private
    fun Project.configurePluginMaven() {
        configure<PublishingExtension> {
            publications.getByName<MavenPublication>("pluginMaven") {
                artifact(tasks.getByName("sourcesJar"))
                artifact(tasks.getByName("javadocJar"))

                plugins.withType<GroovyPlugin> {
                    artifact(tasks.getByName("groovydocJar"))
                }

                pom {
                    licenseGnu2()
                }
            }
        }
    }

}