package me.tbsten.cream.test.architecture.creamKsp.core.sealedCopyGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.sealedCopyGenerator() =
    "SealedCopyGenerator" {
        title = "core/sealedCopy"
        summary = "`@SealedCopy` 生成（`when(this)` 自己 copy、leaf 分類。肥大ファイルは責務単位で複数に分割）"
        example("SealedCopy")
        layout {
            KSP_MAIN_DIR / "core/sealedCopy" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("core.", "options.", "util.", runtime = true, kspApi = true)
    }
