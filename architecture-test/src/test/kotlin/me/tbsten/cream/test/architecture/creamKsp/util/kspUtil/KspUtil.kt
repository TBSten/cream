package me.tbsten.cream.test.architecture.creamKsp.util.kspUtil

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.kspUtil() =
    "KspUtil" {
        title = "util/ksp"
        summary = "KSP API 上の汎用ヘルパ（汎用範囲の KSP API のみ）"
        example("SealedHierarchy")
        layout {
            KSP_MAIN_DIR / "util/ksp" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("util.", runtime = false, kspApi = true)
    }
