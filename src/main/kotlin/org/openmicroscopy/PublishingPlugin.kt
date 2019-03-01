package org.openmicroscopy

import com.google.common.base.CaseFormat
import groovy.lang.GroovyObject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.delegateClosureOf
import org.gradle.kotlin.dsl.get
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.openmicroscopy.PluginHelper.Companion.getRuntimeClasspathConfiguration
import org.openmicroscopy.PluginHelper.Companion.licenseGnu2
import java.text.SimpleDateFormat
import java.util.*
import org.gradle.kotlin.dsl.*


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
            publications {
                create<MavenPublication>(camelCaseName()) {
                    plugins.withType<JavaPlugin> {
                        from(components["java"])
                        artifact(tasks.getByName("sourcesJar"))
                        artifact(tasks.getByName("javadocJar"))
                    }

                    plugins.withType<GroovyPlugin> {
                        artifact(tasks.getByName("groovydocJar"))
                    }

                    pom {
                        licenseGnu2()
                        afterEvaluate {
                            withXml {
                                val repositoriesNode = asNode().appendNode("repositories")
                                repositories.forEach {
                                    if (it is MavenArtifactRepository) {
                                        val repositoryNode = repositoriesNode.appendNode("repository")
                                        repositoryNode.appendNode("id", it.name)
                                        repositoryNode.appendNode("name", it.name)
                                        repositoryNode.appendNode("url", it.url)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private
    fun Project.configureArtifactoryExtension() {
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

    private
    fun Project.resolveProperty(envVarKey: String, projectPropKey: String): String? {
        val propValue = System.getenv()[envVarKey]
        if (propValue != null) {
            return propValue
        }
        return findProperty(projectPropKey).toString()
    }

    private
    fun Project.camelCaseName(): String {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, name)
    }

}
