package me.tbsten.cream.test.architecture.test.architectureTest

import me.tbsten.cream.test.architecture.ARCHITECTURE_DEFINITION_ROOT
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.definitionConstantRole() =
    "DefinitionConstant".role {
        documented = false
        title = "定義の定数"
        summary = "定義で使う定数（モジュール・パッケージのパスと、feature の一覧）"
        example("Paths.kt", "パス定数")
        example("Features.kt", "cream-ksp の feature の一覧")

        layout {
            ARCHITECTURE_DEFINITION_ROOT {
                "Paths".ktFile()
                "Features".ktFile()
            }
        }
    }
