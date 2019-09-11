plugins {
    groovy
    `kotlin-dsl`
    `java-gradle-plugin`
    id("org.openmicroscopy.additional-artifacts")
    id("org.openmicroscopy.additional-repositories")
    id("org.openmicroscopy.plugin-publishing")
}

group = "org.openmicroscopy"
version = "5.5.2-SNAPSHOT"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(fileTree("$projectDir/buildSrc/build/libs").matching {
        include("*.jar")
    })

    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.3")
}

gradlePlugin {
    plugins {
        // Java/Groovy/Kotlin Project plugins
        register("additional-artifacts-plugin") {
            id = "org.openmicroscopy.additional-artifacts"
            implementationClass = "org.openmicroscopy.artifact.AdditionalArtifactsPlugin"
        }
        register("additional-repositories-plugin") {
            id = "org.openmicroscopy.additional-repositories"
            implementationClass = "org.openmicroscopy.artifact.AdditionalRepositoriesPlugin"
        }
        register("functional-test-plugin") {
            id = "org.openmicroscopy.functional-test"
            implementationClass = "org.openmicroscopy.artifact.FunctionalTestPlugin"
        }
        register("plugin-project-plugin") {
            id = "org.openmicroscopy.plugin-project"
            implementationClass = "org.openmicroscopy.artifact.PluginProjectPlugin"
        }
        register("plugin-publishing-plugin") {
            id = "org.openmicroscopy.plugin-publishing"
            implementationClass = "org.openmicroscopy.artifact.PluginPublishingPlugin"
        }

        register("publishing-plugin") {
            id = "org.openmicroscopy.publishing"
            implementationClass = "org.openmicroscopy.artifact.PublishingPlugin"
        }
        register("project-plugin") {
            id = "org.openmicroscopy.project"
            implementationClass = "org.openmicroscopy.artifact.ProjectPlugin"
        }
    }
}

// We need this to pull in compiled classes from the buildSrc jar.
tasks.jar {
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter {
            it.name == "omero-plugin.jar"
        }.map {
            zipTree(it)
        }
    })
}

tasks.sourcesJar {
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter {
            it.name == "omero-plugin-sources.jar"
        }.map {
            zipTree(it)
        }
    })
}
