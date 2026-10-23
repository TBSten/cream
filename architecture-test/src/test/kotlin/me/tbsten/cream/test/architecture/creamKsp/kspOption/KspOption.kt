package me.tbsten.cream.test.architecture.creamKsp.kspOption

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.kspOption() =
    "KspOption" {
        title = "KSP option"
        summary = "KSP オプションのモデルとパース（`CreamOptions` / `EscapeDot` / `CopyFunNamingStrategy`）。全層から参照される横断モデル"
        description = "パース失敗は `core/error` の例外で送出する。KSP API には依存しない。"
        example("CreamOptions")
        layout {
            KSP_MAIN_DIR / "options" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("options.", "util.", "core.error.", runtime = true, kspApi = false)
    }
