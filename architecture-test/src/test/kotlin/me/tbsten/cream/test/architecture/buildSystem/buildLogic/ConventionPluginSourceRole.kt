package me.tbsten.cream.test.architecture.buildSystem.buildLogic

import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.conventionPluginSourceRole() =
    "ConventionPluginSource".role {
        documented = false
        title = "convention plugin"
        summary = "各モジュールが適用する convention plugin と、その DSL ヘルパ"
        example("LintPlugin.kt", "ktlint を設定する plugin")
        example("dsl/Ktlint.kt", "ktlint 設定の DSL")

        layout {
            "buildLogic" / "convention" / "src" / "main" /
                "kotlin" {
                    "*Plugin".ktFile()
                    "dsl" / "*".ktFile()
                }
        }
    }
