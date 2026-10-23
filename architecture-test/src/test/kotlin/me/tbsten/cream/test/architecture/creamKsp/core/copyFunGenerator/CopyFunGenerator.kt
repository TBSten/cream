package me.tbsten.cream.test.architecture.creamKsp.core.copyFunGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.copyFunGenerator() =
    "CopyFunGenerator" {
        title = "core/copyFun"
        summary = "copy 関数生成（class / object / sealed への dispatch、Class / Object / SealedClass 生成）"
        example("Class")
        example("SealedClass")
        layout {
            KSP_MAIN_DIR / "core/copyFun" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("core.", "options.", "util.", runtime = true, kspApi = true)
    }
