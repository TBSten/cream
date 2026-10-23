package me.tbsten.cream.test.architecture.creamKspTest.testing.kotlinCodeLikeString

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.kotlinCodeLikeString() =
    "KotlinCodeLikeString" {
        summary = "Renders values such as `CreamOptions` as Kotlin-code-like strings, for test names and golden files"
        example("BuildKotlinCodeLikeString")
        layout {
            KSP_TEST_DIR / "testing/kotlincodelikestring" / "**" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
