package me.tbsten.cream.test.architecture.test.kspTest

import me.tbsten.cream.test.architecture.KSP_TEST
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.testingSupportRole() =
    "TestingSupport".role {
        title = "テスト支援"
        summary = "テストが使う支援コードと、その smoke test"
        description =
            """
            testing/<用途>/ に用途ごとにまとめる。testing/ 直下には置かない。
            """.trimIndent()
        example("testing/compile/CreamCompilation.kt", "kctfork でのコンパイル")
        example("testing/snapshot/SnapshotAssertion.kt", "golden snapshot との比較")
        example("testing/generator/Generator.kt", "テスト入力のクラスを組み立てる generator")
        example("testing/smoke/CreamCompilationSmokeTest.kt", "支援コード自身の smoke test")

        layout {
            KSP_TEST / "testing" / "*" / "**" / "*".ktFile()
        }
    }
