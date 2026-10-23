package me.tbsten.cream.test.architecture.creamKsp.kspOption

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.importsOnly
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.kspOption() =
    "KspOption" {
        title = "KSP option"
        summary = "The KSP option model and its parsing (`CreamOptions`, `EscapeDot`, ...), referenced from every layer"
        example("CreamOptions")
        layout {
            KSP_MAIN_DIR / "options" / "*".ktFile()
        }
        packageFollowsDirectory()
        importsOnly(
            "me.tbsten.cream.ksp.options.",
            "me.tbsten.cream.ksp.util.",
            "me.tbsten.cream.ksp.core.error.",
            forbidKspApi = true,
        )
    }
