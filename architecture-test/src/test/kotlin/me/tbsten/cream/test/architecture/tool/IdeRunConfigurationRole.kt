package me.tbsten.cream.test.architecture.tool

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.ideRunConfigurationRole() =
    "IdeRunConfiguration".role {
        documented = false
        title = "IDE の実行構成"
        summary = "共有する IntelliJ の Run Configuration"
        example(".run/_cream-ksp_test.run.xml", "cream-ksp のテスト実行")

        layout {
            ".run" / "*.run.xml".file()
        }
    }
