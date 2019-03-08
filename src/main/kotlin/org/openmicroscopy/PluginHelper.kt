package org.openmicroscopy

import com.google.common.base.CaseFormat
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.MavenPom
import org.gradle.kotlin.dsl.*
import java.net.URI

// Note: this should be copied to build.gradle.kts to ensure this plugin can publish itself
class PluginHelper {
    companion object {
        fun getRuntimeClasspathConfiguration(project: Project): Configuration? =
                project.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)

        fun MavenPom.licenseGnu2() = licenses {
            license {
                name.set("GNU General Public License, Version 2")
                url.set("https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")
                distribution.set("repo")
            }
        }

        fun RepositoryHandler.safeAdd(repository: ArtifactRepository?): Boolean {
            if (repository != null) {
                return add(repository)
            }
            return false
        }

        fun Project.createArtifactoryMavenRepo(): MavenArtifactRepository? {
            val artiUrl = resolveProperty("ARTIFACTORY_URL", "artifactoryUrl")
                    ?: return null

            return repositories.maven {
                name = "artifactory"
                url = URI.create(artiUrl)
                credentials {
                    username = resolveProperty("ARTIFACTORY_USER", "artifactoryUser")
                    password = resolveProperty("ARTIFACTORY_PASSWORD", "artifactoryPassword")
                }
            }
        }

        fun Project.createGitlabMavenRepo(): MavenArtifactRepository? {
            val gitlabUrl = resolveProperty("GITLAB_URL", "gitlabUrl")
                    ?: return null

            return repositories.maven {
                name = "gitlab"
                url = URI.create(gitlabUrl)
                credentials(HttpHeaderCredentials::class, Action {
                    // Token specified by
                    val jobToken = System.getenv("CI_JOB_TOKEN")
                    if (jobToken != null) {
                        name = "Job-Token"
                        value = jobToken
                    } else {
                        name = "Private-Token"
                        value = resolveProperty("GITLAB_TOKEN", "gitlabToken")
                    }
                })
            }
        }

        fun Project.createStandardMavenRepo(): MavenArtifactRepository? {
            val releasesRepoUrl =
                    resolveProperty("MAVEN_RELEASES_REPO_URL", "mavenReleasesRepoUrl")
            val snapshotsRepoUrl =
                    resolveProperty("MAVEN_SNAPSHOTS_REPO_URL", "mavenSnapshotsRepoUrl")

            val chosenUrl =
                    (if (hasProperty("release")) releasesRepoUrl else snapshotsRepoUrl) ?: return null

            return repositories.maven {
                url = URI.create(chosenUrl)
                name = "maven"
                credentials {
                    username = resolveProperty("MAVEN_USER", "mavenUser")
                    password = resolveProperty("MAVEN_PASSWORD", "mavenPassword")
                }
            }
        }

        fun Project.resolveProperty(envVarKey: String, projectPropKey: String): String? {
            val propValue = System.getenv(envVarKey)
            if (propValue != null) {
                return propValue
            }

            return findProperty(projectPropKey)?.toString()
        }

        fun Project.camelCaseName(): String {
            return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, name)
        }
    }

}

