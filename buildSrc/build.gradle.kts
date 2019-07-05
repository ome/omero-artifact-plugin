plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "org.openmicroscopy"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.3")
}

gradlePlugin {
    plugins {
        // Plugins for gradle plugins
        register("additional-artifacts-plugin") {
            id = "additional-artifacts"
            implementationClass = "org.openmicroscopy.artifact.AdditionalArtifactsPlugin"
        }
        register("additional-repositories-plugin") {
            id = "additional-repositories"
            implementationClass = "org.openmicroscopy.artifact.AdditionalRepositoriesPlugin"
        }
        register("functional-test-plugin") {
            id = "functional-test"
            implementationClass = "org.openmicroscopy.artifact.FunctionalTestPlugin"
        }
        register("publishing-plugin") {
            id = "publishing"
            implementationClass = "org.openmicroscopy.artifact.PublishingPlugin"
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    description = "Creates a jar of java sources, classified -sources"
    archiveClassifier.set("sources")
    from(sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].allSource)
}

tasks.named("assemble") {
    dependsOn(sourcesJar)
}
