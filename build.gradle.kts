plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    group = "me.tbsten.cream"
    version = rootProject.libs.versions.cream.get()
}
