pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven { url 'https://artifacts.openmicroscopy.org/artifactory/maven' }
        gradlePluginPortal()
    }
}

rootProject.name = "omero-artifact-plugin"
