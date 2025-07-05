import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.vanniktech.mavenPublish)
}

dependencies {
    implementation(project(":cream-runtime"))
    implementation(libs.kspApi)
    implementation(kotlin("reflect"))
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    if (!(gradle.startParameter.taskNames.contains("publishToMavenLocal"))) {
        signAllPublications()
    }

    coordinates(group.toString(), "cream-runtime", version.toString())

    pom {
        name = "cream.kt ksp plugin"
        description = "cream.kt is a KSP Plugin that makes it easy to copy across classes."
        inceptionYear = "2025"
        url = "https://github.com/TBSten/cream/"
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id = "TBSten"
                name = "TBSten"
                url = "https://github.com/TBSten/"
            }
        }
        scm {
            url.set("https://github.com/TBSten/cream/")
            connection.set("scm:git:git://github.com/TBSten/cream/.git")
            developerConnection.set("scm:git:git://github.com/TBSten/cream/.git")
        }
    }
}
