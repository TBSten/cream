package me.tbsten.cream.test.architecture.creamKsp.util.genericUtil

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.genericUtil() =
    "GenericUtil" {
        title = "util"
        summary = "Kotlin-only helpers, reusable outside KSP"
        example("EscapeIdentifier")
        layout {
            KSP_MAIN_DIR / "util" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
