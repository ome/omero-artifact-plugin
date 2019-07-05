/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
package org.openmicroscopy.artifact

import groovy.lang.GroovyObject
import groovy.util.Node
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.openmicroscopy.artifact.dsl.MavenPomExtensions.Companion.licenseGnu2
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.camelCaseName
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.createArtifactoryMavenRepo
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.createGitlabMavenRepo
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.createStandardMavenRepo
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.getRuntimeClasspathConfiguration
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.resolveProperty
import org.openmicroscopy.artifact.dsl.RepositoryHandlerExtensions.Companion.safeAdd
import java.text.SimpleDateFormat
import java.util.*


class PublishingPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyPublishingPlugin()
        configureManifest()
        configureArtifactoryExtension()
        configurePublishingExtensionSimple()
    }

    private
    fun Project.applyPublishingPlugin() {
        apply<MavenPublishPlugin>()
        apply<ArtifactoryPlugin>()
    }

    // ORIGINAL
    /* jar {
        manifest {
            attributes(
                    'Build-Timestamp': new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()),
            'Created-By': "Gradle ${gradle.gradleVersion}",
            'Build-Jdk': "${System.properties['java.version']} (${System.properties['java.vendor']} ${System.properties['java.vm.version']})",
            'Main-Class': 'ome.util.tasks.Run',
            "Class-Path": configurations.runtimeClasspath.collect { it.getName() }.join(' ')
            )
        }
    }*/
    private
    fun Project.configureManifest() {
        plugins.withType<JavaPlugin> {
            tasks.named<Jar>("jar") {
                doFirst {
                    manifest.attributes.putAll(basicManifest())
                }
            }
        }
    }

    private
    fun Project.configurePublishingExtensionSimple() {
        configure<PublishingExtension> {
            repositories {
                safeAdd(createArtifactoryMavenRepo())
                safeAdd(createGitlabMavenRepo())
                safeAdd(createStandardMavenRepo())
            }

            // Publication meant for production and includes docs
            val publication = publications.create<MavenPublication>(camelCaseName()) {
                from(components["java"])
                pom(standardPom())
            }

            plugins.withType<AdditionalArtifactsPlugin> {
                // Publication meant for development, skips any doc generation
                val devPublish = resolveProperty("DEVELOPER_PUBLISH", "developerPublish")
                if (devPublish != null && devPublish.toBoolean()) {
                    publication.artifact(tasks["sourcesJar"])
                } else {
                    // Publication meant for production and includes docs
                    publication.artifact(tasks["sourcesJar"])
                    publication.artifact(tasks["javadocJar"])
                    plugins.withType<GroovyPlugin> {
                        publication.artifact(tasks["groovydocJar"])
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

    private
    fun Project.repositoriesXml(xml: XmlProvider): Node {
        val repositoriesNode = xml.asNode().appendNode("repositories")
        repositories.forEach {
            if (it is MavenArtifactRepository) {
                val repositoryNode = repositoriesNode.appendNode("repository")
                repositoryNode.appendNode("id", it.name)
                repositoryNode.appendNode("name", it.name)
                repositoryNode.appendNode("url", it.url)
            }
        }
        return repositoriesNode
    }

    private
    fun Project.standardPom(): Action<in MavenPom>? {
        return Action {
            licenseGnu2()
            afterEvaluate {
                withXml {
                    repositoriesXml(this)
                }
            }
        }
    }

    private
    fun Project.basicManifest(): Map<String, Any?> {
        return linkedMapOf("Implementation-Title" to project.name.replace("[^A-Za-z0-9]", ""),
                "Implementation-Version" to project.version,
                "Built-Date" to SimpleDateFormat("dd/MM/yyyy").format(Date()),
                "Built-JDK" to System.getProperty("java.version"),
                "Built-Gradle" to project.gradle.gradleVersion,
                "Class-Path" to getRuntimeClasspathConfiguration()?.joinToString(separator = " ") { it.name })
    }

}