package org.openmicroscopy.artifact.dsl

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

class RepositoryHandlerExtensions {
    companion object {
        fun RepositoryHandler.safeAdd(repository: ArtifactRepository?): Boolean {
            if (repository != null) {
                return add(repository)
            }
            return false
        }

        fun RepositoryHandler.safeAdd(repository: Action<in MavenArtifactRepository>?): MavenArtifactRepository? {
            if (repository != null) {
                return maven(repository)
            }
            return null
        }
    }
}