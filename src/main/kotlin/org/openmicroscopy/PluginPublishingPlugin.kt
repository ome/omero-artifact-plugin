package org.openmicroscopy

import groovy.lang.GroovyObject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.*
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.openmicroscopy.PluginHelper.Companion.createArtifactoryMavenRepo
import org.openmicroscopy.PluginHelper.Companion.createGitlabMavenRepo
import org.openmicroscopy.PluginHelper.Companion.createStandardMavenRepo
import org.openmicroscopy.PluginHelper.Companion.licenseGnu2
import org.openmicroscopy.PluginHelper.Companion.resolveProperty
import org.openmicroscopy.PluginHelper.Companion.safeAdd


class PluginPublishingPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyJavaGradlePlugin()
        configurePluginMaven()
        configureArtifactoryExtension()
    }

    private
    fun Project.applyJavaGradlePlugin() {
        apply<MavenPublishPlugin>()
        apply<ArtifactoryPlugin>()
        apply<JavaGradlePluginPlugin>()
    }

    private
    fun Project.configurePluginMaven() {
        afterEvaluate {
            configure<PublishingExtension> {
                repositories {
                    safeAdd(createArtifactoryMavenRepo())
                    safeAdd(createGitlabMavenRepo())
                    safeAdd(createStandardMavenRepo())
                }

                // pluginMaven is task created by MavenPluginPublishPlugin
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

    private
    fun Project.configureArtifactoryExtension() {
        plugins.withType<ArtifactoryPlugin> {
            configure<ArtifactoryPluginConvention> {
                publish(delegateClosureOf<PublisherConfig> {
                    setContextUrl(resolveProperty("ARTIFACTORY_URL", "artifactoryUrl"))
                    repository(delegateClosureOf<GroovyObject> {
                        setProperty("repoKey", resolveProperty("ARTIFACTORY_REPOKEY", "artifactoryRepokey"))
                        setProperty("username", resolveProperty("ARTIFACTORY_USER", "artifactoryUser"))
                        setProperty("password", resolveProperty("ARTIFACTORY_PASSWORD", "artifactoryPassword"))
                    })
                })
            }
        }
    }
}
