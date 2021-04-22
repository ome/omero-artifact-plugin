plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "org.openmicroscopy"
version = "5.5.3-SNAPSHOT"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.3")
}

gradlePlugin {
    plugins {
        register("plugin-project-plugin") {
            id = "org.openmicroscopy.plugin-project"
            implementationClass = "org.openmicroscopy.PluginProjectPlugin"
        }
    }
}
