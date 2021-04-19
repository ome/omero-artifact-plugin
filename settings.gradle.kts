pluginManagement {
    repositories {
        mavenCentral()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
        gradlePluginPortal()
        maven {
            name = "ajoberstar-backup"
            setUrl("https://ajoberstar.github.io/bintray-backup/")
        }
    }
}

rootProject.name = "omero-artifact-plugin"
