package org.openmicroscopy.dsl

import org.gradle.api.publish.maven.MavenPom

class MavenPomExtensions {
    companion object {
        fun MavenPom.licenseGnu2() = licenses {
            license {
                name.set("GNU General Public License, Version 2")
                url.set("https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")
                distribution.set("repo")
            }
        }
    }
}