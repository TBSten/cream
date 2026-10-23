package me.tbsten.cream.test.architecture.creamKsp.core.creamException

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.importsOnly
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.creamException() =
    "CreamException" {
        title = "core/error"
        summary = "The `CreamException` hierarchy: a leaf that depends on util only, so that options can throw it too"
        example("CreamException")
        layout {
            KSP_MAIN_DIR / "core/error" / "*".ktFile()
        }
        packageFollowsDirectory()
        importsOnly("me.tbsten.cream.ksp.util.", "me.tbsten.cream.ksp.core.error.", forbidKspApi = true)
    }
