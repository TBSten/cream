package me.tbsten.cream.test.architecture.creamKsp.core.creamException

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.creamException() =
    "CreamException" {
        title = "core/error"
        summary = "例外階層（`CreamException` / `InvalidCreamUsageException` / `InvalidCreamOptionException` / `UnknownCreamException` と `reportToGithub`）"
        description =
            """
            `util` 以外に依存しない葉。だから `options` からも送出できる（options → core/error の 1 箇所だけパッケージ間の相互参照になるが、
            core/error が葉なので循環しない）。
            """.trimIndent()
        example("CreamException")
        layout {
            KSP_MAIN_DIR / "core/error" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("util.", "core.error.", runtime = false, kspApi = false)
    }
