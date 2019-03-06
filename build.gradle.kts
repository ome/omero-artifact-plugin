import groovy.lang.GroovyObject
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import java.net.URI

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("com.jfrog.artifactory") version "4.9.1"
}

group = "org.openmicroscopy"
version = "5.5.0-SNAPSHOT"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.1")
}

gradlePlugin {
    plugins {
        register("project-plugin") {
            id = "org.openmicroscopy.project"
            implementationClass = "org.openmicroscopy.ProjectPlugin"
        }
        register("additional-repositories-plugin") {
            id = "org.openmicroscopy.additional-repositories"
            implementationClass = "org.openmicroscopy.AdditionalArtifactsPlugin"
        }
        register("additional-artifacts-plugin") {
            id = "org.openmicroscopy.additional-artifacts"
            implementationClass = "org.openmicroscopy.AdditionalArtifactsPlugin"
        }
        register("publishing-plugin") {
            id = "org.openmicroscopy.publishing"
            implementationClass = "org.openmicroscopy.PublishingPlugin"
        }
        register("plugin-project-plugin") {
            id = "org.openmicroscopy.plugin-project"
            implementationClass = "org.openmicroscopy.PluginProjectPlugin"
        }
        register("plugin-publishing-plugin") {
            id = "org.openmicroscopy.plugin-publishing"
            implementationClass = "org.openmicroscopy.PluginPublishingPlugin"
        }
    }
}

tasks {
    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        from(named("javadoc"))
    }
}

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

configure<PublishingExtension> {
    repositories {
        val chosenUrl = resolveProperty("MAVEN_REPO_URL", "mavenRepoUrl")
        if (chosenUrl != null) {
             maven {
                url = URI.create(chosenUrl)
                name = "maven"
                credentials {
                    username = resolveProperty("MAVEN_USER", "mavenUser")
                    password = resolveProperty("MAVEN_PASSWORD", "mavenPassword")
                }
            }
        }
    }
}

project.afterEvaluate {
    configure<PublishingExtension> {
        publications.getByName("pluginMaven", closureOf<MavenPublication> {
            artifact(tasks.getByName("sourcesJar"))
            artifact(tasks.getByName("javadocJar"))
        })
    }
}

fun resolveProperty(envVarKey: String, projectPropKey: String): String? {
    val propValue = System.getenv(envVarKey)
    if (propValue != null) {
        return propValue
    }

    return findProperty(projectPropKey)?.toString()
}
