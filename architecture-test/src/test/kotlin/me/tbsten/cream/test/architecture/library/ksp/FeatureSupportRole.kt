package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.features
import me.tbsten.cream.test.architecture.support.`1 ファイル 300 行以内`
import me.tbsten.cream.test.architecture.support.`feature の補助はほかの feature と composition root に依存しない`
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.featureSupportRole() =
    "FeatureSupport".role {
        title = "アノテーションごとの補助"
        summary = "1 つのアノテーションの処理だけが使う補助。ファイル名は <Name> で始める"
        example("ChildOptionalsDiagnostics.kt", "@ChildOptionals 専用の診断")

        `1 ファイル 300 行以内`()
        `feature の補助はほかの feature と composition root に依存しない`()

        layout {
            KSP_MAIN {
                "feature" {
                    features.forEach { feature ->
                        val name = feature.replaceFirstChar { it.uppercase() }
                        feature / "$name*".ktFile()
                    }
                }
            }
        }
    }
