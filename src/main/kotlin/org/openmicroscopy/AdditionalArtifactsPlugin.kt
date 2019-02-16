package org.openmicroscopy


import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.*
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.*

class AdditionalArtifactsPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        plugins.withType(JavaPlugin::class) {
            val sourceSets = the<SourceSetContainer>()
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            val javadoc by tasks.existing(Javadoc::class) {
                val stdOpts = options as StandardJavadocDocletOptions
                stdOpts.addStringOption("Xdoclint:none", "-quiet")
                if (JavaVersion.current().isJava9Compatible) {
                    stdOpts.addBooleanOption("html5", true)
                }
            }

            tasks.register("sourcesJar", Jar::class) {
                archiveClassifier.set("sources")
                from(main.allSource)
            }

            tasks.register("javadocJar", Jar::class) {
                archiveClassifier.set("javadoc")
                from(javadoc)
            }
        }

        plugins.withType(GroovyPlugin::class) {
            val groovydoc: Groovydoc by tasks

            tasks.register("groovydocJar", Jar::class) {
                archiveClassifier.set("groovydoc")
                from(groovydoc)
            }
        }
    }
}