package me.tbsten.cream.test.architecture.creamKsp.util.genericUtil

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.genericUtil() =
    "GenericUtil" {
        title = "util"
        summary = "Kotlin stdlib だけに依存するヘルパ。KSP 以外の文脈でも再利用できる"
        example("EscapeIdentifier")
        layout {
            KSP_MAIN_DIR / "util" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("util.", runtime = false, kspApi = false)
    }
