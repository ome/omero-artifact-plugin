plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "org.openmicroscopy"
version = "5.7.2-SNAPSHOT"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    maven {
      setUrl("https://artifacts.openmicroscopy.org/artifactory/maven")
    }
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.34.1")
    implementation("org.ajoberstar.grgit:grgit-core:5.3.0") 
    implementation("org.ajoberstar.grgit:grgit-gradle:5.3.0") 
    implementation("org.ajoberstar.git-publish:gradle-git-publish:4.2.2")
}

gradlePlugin {
    plugins {
        register("plugin-project-plugin") {
            id = "org.openmicroscopy.plugin-project"
            implementationClass = "org.openmicroscopy.PluginProjectPlugin"
        }
    }
}
