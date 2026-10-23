plugins {
    alias(libs.plugins.kotlinJvm)
    // Adds a task per processor (katachiDocs, katachiTemplate, and so on). It does not add
    // katachi as a dependency, so the dependencies { } block below is still needed.
    alias(libs.plugins.katachi)
    id("buildLogic.lint")
}

kotlin {
    jvmToolchain(17)

    // Kotlin before 2.4 needs this on the calling side to use context parameters.
    // Every katachi DSL entry point (mainSourceSet / ktFile ...) is a contextual
    // declaration, so without it you cannot write a single one. Drop this line once
    // the project moves to Kotlin 2.4 or later - from 2.4 on the flag warns that it is
    // redundant.
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

katachi {
    // The fully qualified name of the top-level val holding architecture { }. Update it
    // if you rename the val or move it to another package.
    architecture = "me.tbsten.cream.test.architecture.projectArchitecture"
    processors {
        docs {
            // Committed, so that the architecture pages are readable without a build. CI runs
            // `katachiDocs --arg mode=check`, which fails when they no longer match the definition.
            outputDir = rootProject.layout.projectDirectory.dir("doc/internal/architecture")
        }
    }
}

tasks.test {
    // Never let this task be cached. katachi walks the whole repository when the test
    // runs, but the only inputs Gradle can see are the test sources and the classpath of
    // this module. A file added or moved anywhere else leaves the key unchanged, so Gradle
    // answers UP-TO-DATE or FROM-CACHE and the check never runs. A guard that silently
    // passes is worse than no guard at all.
    outputs.upToDateWhen { false }
    outputs.cacheIf { false }

    // kotest runs on the JUnit Platform.
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        // The violation list lives in the AssertionError message. Without FULL you only get
        // "KatachiArchitectureAssertionError at ProjectArchitectureTest.kt:12", and reading the
        // detail means opening build/test-results/**/*.xml (the same goes for CI logs).
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        // Warnings such as MissingDescription are printed to stdout.
        showStandardStreams = true
    }
}

dependencies {
    testImplementation(libs.katachi)
    testImplementation(libs.katachiKonsist)
    testImplementation(libs.kotest)
    testImplementation(libs.kotestRunnerJunit5)
}
