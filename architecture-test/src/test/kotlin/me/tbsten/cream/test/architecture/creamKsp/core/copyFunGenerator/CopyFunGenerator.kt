package me.tbsten.cream.test.architecture.creamKsp.core.copyFunGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.copyFunGenerator() =
    "CopyFunGenerator" {
        title = "core/copyFun"
        summary = "Copy function generation, dispatched by target kind (class / object / sealed)"
        example("Class")
        example("SealedClass")
        layout {
            KSP_MAIN_DIR / "core/copyFun" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
