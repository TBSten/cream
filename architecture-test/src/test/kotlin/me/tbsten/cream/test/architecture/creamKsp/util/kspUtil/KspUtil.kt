package me.tbsten.cream.test.architecture.creamKsp.util.kspUtil

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.kspUtil() =
    "KspUtil" {
        title = "util/ksp"
        summary = "Generic helpers over the KSP API"
        example("SealedHierarchy")
        layout {
            KSP_MAIN_DIR / "util/ksp" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
