package org.openmicroscopy

import groovy.lang.GroovyObject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.openmicroscopy.PluginHelper.Companion.getRuntimeClasspathConfiguration
import java.text.SimpleDateFormat
import java.util.*

class PublishingPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyPublishingPlugin()

        plugins.withType<JavaPlugin> {
            configureManifest()
            configurePublishingExtension()
            configureArtifactoryExtension()
        }
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
        tasks.named<Jar>("jar").configure {
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

    private
    fun Project.configurePublishingExtension() {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    artifact(tasks.getByName("sourcesJar"))
                    artifact(tasks.getByName("javadocJar"))

                    plugins.withType(GroovyPlugin::class) {
                        artifact(tasks.getByName("groovydocJar"))
                    }

                    pom {
                        licenses {
                            license {
                                name.set("GNU General Public License, Version 2")
                                url.set("https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")
                                distribution.set("repo")
                            }
                        }
                        configureRepositories(repositories)
                    }
                }
            }
        }
    }

    private
    fun Project.configureArtifactoryExtension() {
        configure<ArtifactoryPluginConvention> {
            publish(delegateClosureOf<PublisherConfig> {
                setContextUrl("https://artifacts.openmicroscopy.org/artifactory")
                repository(delegateClosureOf<GroovyObject> {
                    setProperty("repoKey", "")
                    setProperty("username", resolveProperty("ARTIFACTORY_USER", "artifactorUser"))
                    setProperty("password", resolveProperty("ARTIFACTORY_PASSWORD", "artifactorPassword"))
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
    fun Project.configureRepositories(repositoryHandler: RepositoryHandler) {
        for (repo in repositories) {
            if (repo is MavenArtifactRepository) {
                repositoryHandler.maven {
                    name = repo.name
                    url = repo.url
                }
            }
        }
    }

}
