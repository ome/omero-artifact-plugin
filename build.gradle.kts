plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    org.openmicroscopy.`plugin-project`
}

group = "org.openmicroscopy"
version = "5.5.3-SNAPSHOT"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.3")
}

gradlePlugin {
    plugins {
        register("project-plugin") {
            id = "org.openmicroscopy.project"
            implementationClass = "org.openmicroscopy.ProjectPlugin"
        }
        register("additional-repositories-plugin") {
            id = "org.openmicroscopy.additional-repositories"
            implementationClass = "org.openmicroscopy.AdditionalRepositoriesPlugin"
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
        register("release-plugin") {
            id = "org.openmicroscopy.release"
            implementationClass = "org.openmicroscopy.ReleasePlugin"
        }
    }
}
