import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "me.tbsten.cream.buildLogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
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
