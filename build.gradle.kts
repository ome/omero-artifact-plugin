plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "org.openmicroscopy"
version = "5.5.0-SNAPSHOT"

repositories {
    jcenter()
    mavenLocal()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.1")
    testImplementation("junit:junit:4.12")
}
