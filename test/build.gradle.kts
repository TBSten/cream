plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ksp)
}

dependencies {
    testImplementation(libs.kotest)
    testImplementation(libs.kotlinTest)

    testImplementation(project(":cream-runtime"))
    ksp(project(":cream-ksp"))
}

ksp {
//    arg("cream.copyFunNamePrefix", "transitionTo")
//    arg("cream.copyFunNamingStrategy", "full-name")
//    arg("cream.escapeDot", "replace-to-underscore")
}
