package org.openmicroscopy.artifact

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*

class FunctionalTestPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val sourceSets = project.the<JavaPluginConvention>().sourceSets
        val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        val testRuntimeClasspath by configurations

        val functionalTestSourceSet = sourceSets.create("functionalTest") {
            java.srcDir("src/functionalTest/java")
            resources.srcDir("src/functionalTest/resources")
            compileClasspath += main.output + testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }

        plugins.withType<GroovyPlugin> {
            val groovySourceSet = DslObject(main).convention.getPlugin(GroovySourceSet::class)
            groovySourceSet.groovy.srcDir("src/functTest/groovy")
        }

        val functionalTest by tasks.creating(Test::class) {
            description = "Runs the functional tests"
            group = "verification"
            testClassesDirs = functionalTestSourceSet.output.classesDirs
            classpath = functionalTestSourceSet.runtimeClasspath
            mustRunAfter("test")
            testLogging {
                showStandardStreams = true
                events("started", "passed", "failed")
            }
        }

        tasks["check"].dependsOn(functionalTest)
    }
}
