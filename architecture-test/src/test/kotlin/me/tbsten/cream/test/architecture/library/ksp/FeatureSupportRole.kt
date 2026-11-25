package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.cream.test.architecture.features
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.featureSupportRole() =
    "FeatureSupport".role {
        title = "アノテーションごとの補助"
        summary = "1 つのアノテーションの処理だけが使う補助。ファイル名は <Name> で始める"
        example("ChildOptionalsDiagnostics.kt", "@ChildOptionals 専用の診断")

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
