plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "org.openmicroscopy"
version = "5.5.0-SNAPSHOT"

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
        register("additional-artifacts-plugin") {
            id = "org.openmicroscopy.additional-artifacts"
            implementationClass = "org.openmicroscopy.AdditionalArtifactsPlugin"
        }
        register("publishing-plugin") {
            id = "org.openmicroscopy.publishing"
            implementationClass = "org.openmicroscopy.PublishingPlugin"
        }
    }
}

tasks {
    create("sourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    create("javadocJar", Jar::class) {
        archiveClassifier.set("javadoc")
        from(named("javadoc"))
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.getByName("sourcesJar"))
            artifact(tasks.getByName("javadocJar"))

            pom {
                licenses {
                    license {
                        name.set("GNU General Public License, Version 2")
                        url.set("https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")
                        distribution.set("repo")
                    }
                }
            }
        }
    }
}