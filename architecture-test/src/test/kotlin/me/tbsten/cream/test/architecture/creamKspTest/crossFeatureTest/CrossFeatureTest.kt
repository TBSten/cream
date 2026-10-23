package me.tbsten.cream.test.architecture.creamKspTest.crossFeatureTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.crossFeatureTest() =
    "CrossFeatureTest" {
        title = "Cross-feature test"
        summary = "フィーチャー横断（複数アノテ併用 / 複数エラー同時）"
        example("MultipleDiagnosticsTest")
        layout {
            KSP_TEST_DIR / "MultipleDiagnosticsTest".ktFile()
        }
        packageFollowsDirectory()
        isInternalFreeSpec()
    }
