@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.library.ksp.kspProcessorAndProvider

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent

fun DeclarationContainerScope.kspProcessorProviderRole() =
    "KspProcessorProvider".role {
        documented = false
        `SymbolProcessorProvider の実装だけを置く`()

        `1 ファイル 300 行以内`()

        layout {
            KSP_MAIN / "CreamSymbolProcessorProvider".ktFile()
        }
    }

private fun RoleScope.`SymbolProcessorProvider の実装だけを置く`() =
    "SymbolProcessorProvider の実装だけを置く".ktFileContent {
        `package`("me.tbsten.cream.ksp")

        `import`("com.google.devtools.ksp.processing.**")

        required(public, `class`, fileName).superclass("SymbolProcessorProvider") {
            required(override, `fun`, "create")("environment: SymbolProcessorEnvironment").returns("SymbolProcessor")
        }
    }
