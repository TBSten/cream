plugins {
    alias(libs.plugins.kotlinJvm)
    id("buildLogic.lint")
}

// Test-only module: it deliberately has no `src/main`. Its whole purpose is to host Konsist specs
// that inspect the *whole* repository (every module, every source set), which no single existing
// module can do without dragging that module's compile classpath along.
kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.kotest)
    testImplementation(libs.kotestRunnerJunit5)
}

/**
 * Every Kotlin source file Konsist can reach (see `ProjectScope.kt` for the matching runtime
 * filter). Declared as a task input because Konsist reads the repository from disk at *test
 * runtime*: without this, Gradle sees no input change when another module's sources are edited and
 * keeps reporting this task as UP-TO-DATE, so architecture violations would slip through.
 */
val repositorySources =
    fileTree(rootDir) {
        // Every module in settings.gradle.kts is top-level today (`cream-ksp/src/…`); the second
        // pattern keeps a nested module (`a/b/src/…`) covered without another edit here.
        include("*/src/**/*.kt", "*/*/src/**/*.kt")
        // Mirrors the runtime exclusions in ProjectScope.kt. `.local/` is git-ignored scratch space
        // that happens to hold Gradle projects with real src/ layouts; without excluding it here,
        // unrelated experiments would invalidate this task.
        exclude("buildLogic/**", ".local/**", "**/build/**")
    }

tasks.named<Test>("test") {
    useJUnitPlatform()
    // Konsist parses every Kotlin file in the repository into a PSI tree; the default 512m worker
    // heap is tight for that.
    maxHeapSize = "1g"
    inputs
        .files(repositorySources)
        .withPropertyName("repositorySources")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
