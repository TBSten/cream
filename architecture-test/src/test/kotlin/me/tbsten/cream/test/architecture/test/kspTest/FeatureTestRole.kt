package me.tbsten.cream.test.architecture.test.kspTest

import me.tbsten.cream.test.architecture.KSP_TEST
import me.tbsten.cream.test.architecture.features
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.featureTestRole() =
    "FeatureTest".role {
        title = "アノテーションごとのテスト"
        summary = "1 つのアノテーションを kctfork でコンパイルして検証するテストと、その入力"
        description =
            """
            feature/<アノテーション名>/（Features.kt の features にあるアノテーション名だけ）の直下にテストクラス（<Name>*Test.kt。テストの種類は縛らない）、
            サブディレクトリにその入力や補助を置く。テストの種類を足しても定義は変えなくてよい。
            """.trimIndent()
        example("copyTo/CopyToSnapshotTest.kt", "scenario × options の golden snapshot")
        example("copyTo/scenario/Generics.kt", "ジェネリクスの scenario")

        layout {
            KSP_TEST {
                "feature" {
                    features.forEach { feature ->
                        val name = feature.replaceFirstChar { it.uppercase() }
                        feature {
                            "$name*Test".ktFile()
                            "*" / "**" / "*".ktFile()
                        }
                    }
                }
            }
        }
    }
