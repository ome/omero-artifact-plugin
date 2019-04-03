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
package org.openmicroscopy

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
import org.openmicroscopy.PluginHelper.Companion.camelCaseName
import org.openmicroscopy.PluginHelper.Companion.createArtifactoryMavenRepo
import org.openmicroscopy.PluginHelper.Companion.createGitlabMavenRepo
import org.openmicroscopy.PluginHelper.Companion.createStandardMavenRepo
import org.openmicroscopy.PluginHelper.Companion.getRuntimeClasspathConfiguration
import org.openmicroscopy.PluginHelper.Companion.licenseGnu2
import org.openmicroscopy.PluginHelper.Companion.resolveProperty
import org.openmicroscopy.PluginHelper.Companion.safeAdd
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.set


class PublishingPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyPublishingPlugin()
        configureManifest()
        configurePublishingExtension()
        configureArtifactoryExtension()
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
                manifest {
                    attributes["Implementation-Title"] = name.replace(Regex("[^A-Za-z0-9]"), "")
                    attributes["Implementation-Version"] = project.version
                    attributes["Built-By"] = System.getProperty("user.name")
                    attributes["Built-Date"] = SimpleDateFormat("dd/MM/yyyy").format(Date())
                    attributes["Built-JDK"] = System.getProperty("java.version")
                    attributes["Built-Gradle"] = gradle.gradleVersion
                    attributes["Class-Path"] = getRuntimeClasspathConfiguration(project)
                            ?.joinToString(separator = " ") { it.name }
                }
            }
        }
    }

    private
    fun Project.configurePublishingExtension() {
        configure<PublishingExtension> {
            repositories {
                safeAdd(createArtifactoryMavenRepo())
                safeAdd(createGitlabMavenRepo())
                safeAdd(createStandardMavenRepo())
            }

            publications {
                create<MavenPublication>("${camelCaseName()}Binary") {
                    plugins.withType<JavaPlugin> {
                        from(components["java"])
                    }
                    pom(standardPom())
                }

                create<MavenPublication>("${camelCaseName()}BinaryAndSources") {
                    plugins.withType<JavaPlugin> {
                        from(components["java"])
                        artifact(tasks.getByName("sourcesJar"))
                    }
                    pom(standardPom())
                }

                create<MavenPublication>(camelCaseName()) {
                    plugins.withType<JavaPlugin> {
                        from(components["java"])
                        artifact(tasks.getByName("sourcesJar"))
                        artifact(tasks.getByName("javadocJar"))
                    }
                    plugins.withType<GroovyPlugin> {
                        artifact(tasks.getByName("groovydocJar"))
                    }
                    pom(standardPom())
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
}
