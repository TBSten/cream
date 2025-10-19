plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("buildLogic.lint")
}

kotlin {
    compilerOptions.optIn.addAll(
        "com.google.devtools.ksp.KspExperimental",
        "me.tbsten.cream.InternalCreamApi",
    )
}

dependencies {
    implementation(project(":cream-runtime"))
    implementation(project(":cream-ksp:shared"))
    implementation(libs.kspApi)
    implementation(kotlin("reflect"))
    testImplementation(libs.kotlinTest)
    testImplementation(libs.mockk)
}

mavenPublishing {
    publishToMavenCentral()

    if (!(gradle.startParameter.taskNames.contains("publishToMavenLocal"))) {
        signAllPublications()
    }

    coordinates(group.toString(), "cream-ksp", version.toString())

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
