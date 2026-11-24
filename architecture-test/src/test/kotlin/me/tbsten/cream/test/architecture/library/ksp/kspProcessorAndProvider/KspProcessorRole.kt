@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.library.ksp.kspProcessorAndProvider

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent

fun DeclarationContainerScope.kspProcessorRole() =
    "KspProcessor".role {
        title = "SymbolProcessor"
        summary = "KSP から呼ばれ、全 feature の processXxx() を呼び分ける合成ルート"
        example("CreamSymbolProcessor.kt", "process() で各 feature を呼ぶ")

        `SymbolProcessor の実装だけを置く`()

        `1 ファイル 300 行以内`()

        layout {
            KSP_MAIN / "CreamSymbolProcessor".ktFile()
        }
    }

private fun RoleScope.`SymbolProcessor の実装だけを置く`() =
    "SymbolProcessor の実装だけを置く".ktFileContent {
        `package`("me.tbsten.cream.ksp")

        `import`("com.google.devtools.ksp.**")
        `import`("me.tbsten.cream.ksp.core.error.**")
        `import`("me.tbsten.cream.ksp.feature.*.process*")
        `import`("me.tbsten.cream.ksp.options.**")

        required(internal, `class`, fileName).superclass("SymbolProcessor") {
            required(override, `fun`, "process")("resolver: Resolver").returns("List<KSAnnotated>")
            allow(internal, `val`, "*")
            allow(private, anythingElse)
        }
    }
