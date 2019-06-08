package org.openmicroscopy.dsl

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository

class RepositoryHandlerExtensions {
    companion object {
        fun RepositoryHandler.safeAdd(repository: ArtifactRepository?): Boolean {
            if (repository != null) {
                return add(repository)
            }
            return false
        }
    }
}