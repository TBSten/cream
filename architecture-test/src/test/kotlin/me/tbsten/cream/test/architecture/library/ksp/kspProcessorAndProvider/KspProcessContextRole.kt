@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.library.ksp.kspProcessorAndProvider

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent

fun DeclarationContainerScope.kspProcessContextRole() =
    "KspProcessContext".role {
        title = "ProcessContext"
        summary = "各 feature に渡す {resolver, options, codeGenerator, logger} の束"
        example("ProcessContext.kt", "context parameter で feature に渡す")

        `feature と core に依存しない 4 つの依存の束`()

        layout {
            KSP_MAIN / "ProcessContext".ktFile()
        }
    }

private fun RoleScope.`feature と core に依存しない 4 つの依存の束`() =
    "feature と core に依存しない 4 つの依存の束".ktFileContent {
        `package`("me.tbsten.cream.ksp")

        `import`("com.google.devtools.ksp.processing.**")
        `import`("me.tbsten.cream.ksp.options.CreamOptions")

        required(internal, `class`, fileName) {
            required(`val`, "resolver").type("Resolver")
            required(`val`, "options").type("CreamOptions")
            required(`val`, "codeGenerator").type("CodeGenerator")
            required(`val`, "logger").type("KSPLogger")
        }
    }
