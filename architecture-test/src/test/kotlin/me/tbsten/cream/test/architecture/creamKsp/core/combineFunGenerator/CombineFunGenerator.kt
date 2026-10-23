package me.tbsten.cream.test.architecture.creamKsp.core.combineFunGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.combineFunGenerator() =
    "CombineFunGenerator" {
        title = "core/combineFun"
        summary = "Combine function generation (N sources → 1 target)"
        example("CombineToClass")
        layout {
            KSP_MAIN_DIR / "core/combineFun" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
