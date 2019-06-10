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

import org.ajoberstar.gradle.git.base.GrgitPlugin
import org.ajoberstar.gradle.git.release.base.ReleasePluginExtension
import org.ajoberstar.gradle.git.release.base.ReleaseVersion
import org.ajoberstar.gradle.git.release.base.TagStrategy
import org.ajoberstar.gradle.git.release.opinion.OpinionReleasePlugin
import org.ajoberstar.gradle.git.release.opinion.Strategies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class ReleasePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyGrgitPlugin()
        configureReleasePluginExtension()
    }

    private
    fun Project.applyGrgitPlugin() {
        apply<OpinionReleasePlugin>()
        apply<GrgitPlugin>()
    }

    private
    fun Project.configureReleasePluginExtension() {
        configure<ReleasePluginExtension> {
            versionStrategy(Strategies.getFINAL())
            defaultVersionStrategy = Strategies.getSNAPSHOT()
            tagStrategy(delegateClosureOf<TagStrategy> {
                generateMessage = closureOf<ReleaseVersion> {
                    "Version ${project.version}"
                }
            })
        }
    }
}
