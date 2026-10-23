package me.tbsten.cream.test.architecture.creamKsp.core.combineFunGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.combineFunGenerator() =
    "CombineFunGenerator" {
        title = "core/combineFun"
        summary = "combine 関数生成（N source → 1 target、CombineToClass、combine の KDoc 例）"
        example("CombineToClass")
        layout {
            KSP_MAIN_DIR / "core/combineFun" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("core.", "options.", "util.", runtime = true, kspApi = true)
    }
