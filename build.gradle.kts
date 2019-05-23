plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    org.openmicroscopy.`plugin-project`
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
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.3")
    implementation("org.ajoberstar:grgit:1.9.1") {
        setForce(true)
    }
    implementation("org.ajoberstar:gradle-git:1.7.1")
    implementation("org.ajoberstar:gradle-git-publish:0.3.3")
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
