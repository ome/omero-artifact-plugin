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
package org.openmicroscopy.artifact

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.createArtifactoryMavenRepo
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.createGitlabMavenRepo
import org.openmicroscopy.artifact.dsl.ProjectExtensions.Companion.createStandardMavenRepo
import org.openmicroscopy.artifact.dsl.RepositoryHandlerExtensions.Companion.safeAdd
import java.net.URI

class AdditionalRepositoriesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        repositories {
            safeAdd(createArtifactoryMavenRepo())
            safeAdd(createGitlabMavenRepo())
            safeAdd(createStandardMavenRepo())
        }
    }
}