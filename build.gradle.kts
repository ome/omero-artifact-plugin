plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    org.openmicroscopy.`plugin-project`
}

group = "org.openmicroscopy"
version = "5.5.5-SNAPSHOT"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    mavenCentral()
    maven {
      setUrl("https://artifacts.openmicroscopy.org/artifactory/maven")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.3")
    implementation("org.ajoberstar.grgit:grgit-core:5.3.0") 
    implementation("org.ajoberstar.grgit:grgit-gradle:5.3.0") 
    implementation("org.ajoberstar.git-publish:gradle-git-publish:4.2.2")
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
