/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
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