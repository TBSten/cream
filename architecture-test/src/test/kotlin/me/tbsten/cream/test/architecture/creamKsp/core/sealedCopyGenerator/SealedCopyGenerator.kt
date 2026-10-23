package me.tbsten.cream.test.architecture.creamKsp.core.sealedCopyGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.sealedCopyGenerator() =
    "SealedCopyGenerator" {
        title = "core/sealedCopy"
        summary = "`@SealedCopy` self-copy generation"
        example("SealedCopy")
        layout {
            KSP_MAIN_DIR / "core/sealedCopy" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
