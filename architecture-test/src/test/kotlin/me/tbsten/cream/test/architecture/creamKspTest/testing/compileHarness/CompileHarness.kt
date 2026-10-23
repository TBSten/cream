package me.tbsten.cream.test.architecture.creamKspTest.testing.compileHarness

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.compileHarness() =
    "CompileHarness" {
        summary = "`compileWithCream`（KotlinCompilation + `useKsp2`）/ `runCompileSnapshotTest` / 結果ラッパー"
        example("CreamCompilation")
        layout {
            KSP_TEST_DIR / "testing/compile" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
