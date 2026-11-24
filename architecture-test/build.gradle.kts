plugins {
    // alias(libs.plugins.kotlin.jvm) と書かない。build-logic（includeBuild）経由で Kotlin plugin が
    // classpath にあると "already on the classpath with an unknown version" で失敗する。バージョンなしの
    // kotlin("jvm") か、カタログに寄せるなら id(libs.plugins.kotlin.jvm.get().pluginId) を使う。
    kotlin("jvm")
    alias(libs.plugins.katachi)
    id("buildLogic.lint")
}

kotlin {
    jvmToolchain(17)

    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

katachi {
    architecture = "me.tbsten.cream.test.architecture.projectArchitecture"
}

tasks.test {
    outputs.upToDateWhen { false }
    outputs.cacheIf { false }

    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

dependencies {
    testImplementation(libs.katachi)
    testImplementation(libs.katachiKonsist)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.kotestRunnerJunit5)
    testRuntimeOnly(libs.junit.platform.launcher)
}
