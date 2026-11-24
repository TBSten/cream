package me.tbsten.cream.test.architecture.test.architectureTest

import me.tbsten.cream.test.architecture.ARCHITECTURE_DEFINITION_ROOT
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.projectArchitectureTestRole() =
    "ProjectArchitectureTest".role {
        documented = false
        title = "定義のテスト"
        summary = "katachi の定義を実行するテスト"
        example("ProjectArchitectureTest.kt", "定義を assert する")

        layout {
            ARCHITECTURE_DEFINITION_ROOT / "ProjectArchitectureTest".ktFile()
        }
    }
