package org.openmicroscopy


import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.*

class AdditionalArtifactsPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        plugins.withType<JavaPlugin> {
            val sourceSets = the<SourceSetContainer>()

            val javadoc by tasks.named<Javadoc>("javadoc") {
                // Configure java doc options
                val stdOpts = options as StandardJavadocDocletOptions
                stdOpts.addStringOption("Xdoclint:none", "-quiet")
                if (JavaVersion.current().isJava9Compatible) {
                    stdOpts.addBooleanOption("html5", true)
                }
            }

            tasks.register<Jar>("sourcesJar") {
                description = "Creates a jar of java sources, classified -sources"
                archiveClassifier.set("sources")
                from(sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].allSource)
            }

            tasks.register<Jar>("javadocJar") {
                description = "Creates a jar of java docs, classified -javadoc"
                archiveClassifier.set("javadoc")
                from(javadoc)
            }
        }

        plugins.withType<GroovyPlugin> {
            val groovydoc by tasks.existing(Groovydoc::class)

            tasks.register<Jar>("groovydocJar") {
                description = "Creates a jar of groovy docs, classified -groovydoc"
                archiveClassifier.set("groovydoc")
                from(groovydoc)
            }
        }
    }
}
