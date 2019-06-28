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

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.reckon.gradle.ReckonExtension
import org.ajoberstar.reckon.gradle.ReckonPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class ReleasePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyReckonPlugin()
        configureReleasePluginExtension()
    }

    private
    fun Project.applyReckonPlugin() {
        apply<ReckonPlugin>()
    }

    private
    fun Project.configureReleasePluginExtension() {
        configure<ReckonExtension> {
            scopeFromProp()
            stageFromProp("alpha", "beta", "rc", "final")
        }

        // set default to patch, saying as that's what we tend to
        // work towards first
        extra["reckon.scope"] = "patch"

        // safety checks before releasing
        tasks.named(ReckonPlugin.TAG_TASK).configure {
            doFirst {
                val grgit = (project.findProperty("grgit")
                        ?: throw GradleException("Can't find grgit")) as Grgit
                val version = version.toString()

                if (grgit.branch.current().name != "master") {
                    throw IllegalStateException("Can only release from master.")
                }

                if (!version.contains('-')) {
                    val head = grgit.head()
                    val tagsOnHead = grgit.tag.list().filter { it.commit == head }
                    if (tagsOnHead.find { it.name.startsWith("$version-rc.") } == null) {
                        throw IllegalStateException("Must release an rc of this commit before making a final.")
                    }
                }
            }
        }

        tasks.matching { it.name == "check" }.configureEach {
            tasks.getByName(ReckonPlugin.TAG_TASK).dependsOn(this)
        }
    }
}
