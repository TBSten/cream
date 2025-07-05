plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    implementation(project(":cream-runtime"))
    implementation(libs.kspApi)
}
