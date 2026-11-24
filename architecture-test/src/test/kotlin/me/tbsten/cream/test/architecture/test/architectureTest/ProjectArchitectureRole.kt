package me.tbsten.cream.test.architecture.test.architectureTest

import me.tbsten.cream.test.architecture.ARCHITECTURE_DEFINITION_ROOT
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.projectArchitectureRole() =
    "ProjectArchitecture".role {
        documented = false
        title = "定義の入口"
        summary = "全 group を集める architecture { } の入口"
        example("ProjectArchitecture.kt", "定義の入口")

        layout {
            ARCHITECTURE_DEFINITION_ROOT / "ProjectArchitecture".ktFile()
        }
    }
