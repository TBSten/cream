plugins {
    `kotlin-dsl`
}

group = "me.tbsten.cream.buildLogic"

kotlin {
    // Compile against a provisioned JDK 17 (configures both the Java and Kotlin toolchains) so the
    // included build does not depend on the JDK that happens to launch Gradle. Provisioning is
    // enabled by the foojay resolver in buildLogic/settings.gradle.kts.
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.kotlinGradlePluginApi)
    implementation(libs.ktlintGradlePlugin)
}

gradlePlugin {
    plugins {
        register("lint") {
            id = "buildLogic.lint"
            implementationClass = "LintPlugin"
        }
    }
}
