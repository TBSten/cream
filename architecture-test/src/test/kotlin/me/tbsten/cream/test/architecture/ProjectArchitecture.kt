package me.tbsten.cream.test.architecture

import me.tbsten.cream.test.architecture.architectureTest.architectureTest
import me.tbsten.cream.test.architecture.buildLogic.buildLogic
import me.tbsten.cream.test.architecture.creamKsp.creamKsp
import me.tbsten.cream.test.architecture.creamKspTest.creamKspTest
import me.tbsten.cream.test.architecture.creamRuntime.creamRuntime
import me.tbsten.cream.test.architecture.documentation.documentation
import me.tbsten.cream.test.architecture.testModule.testModule
import me.tbsten.cream.test.architecture.tools.tools
import me.tbsten.katachi.dsl.architecture
import me.tbsten.katachi.dsl.gradle.gradle

val projectArchitecture =
    architecture {
        title = "cream.kt"
        description =
            """
            cream.kt is a KSP plugin that generates cross-class copy functions.
            `cream-runtime` holds the annotations users write, `cream-ksp` is the processor that reads them,
            and `test` is a multiplatform module that compiles real annotated classes and calls the generated functions.
            """.trimIndent()

        // Gradle's own files: the wrapper, settings, every module's build script, gradle.properties, the version catalog.
        gradle()

        creamRuntime()
        creamKsp()
        creamKspTest()
        testModule()
        architectureTest()
        buildLogic()
        documentation()
        tools()
    }
