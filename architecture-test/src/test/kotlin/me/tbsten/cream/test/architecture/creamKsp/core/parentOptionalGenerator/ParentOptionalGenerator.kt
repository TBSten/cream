package me.tbsten.cream.test.architecture.creamKsp.core.parentOptionalGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.dependsOnlyOn
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.cream.test.architecture.support.staysWithinLineBudget
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.parentOptionalGenerator() =
    "ParentOptionalGenerator" {
        title = "core/parentOptional"
        summary = "`@ParentOptional` / `@ChildOptionals` 生成（sealed 親への nullable 拡張プロパティ accessor。両 feature が共有）"
        example("ParentOptionalAccessor")
        layout {
            KSP_MAIN_DIR / "core/parentOptional" / "*".ktFile()
        }
        packageFollowsDirectory()
        staysWithinLineBudget()
        dependsOnlyOn("core.", "options.", "util.", runtime = true, kspApi = true)
    }
