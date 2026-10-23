package me.tbsten.cream.test.architecture.creamKsp.core.parentOptionalGenerator

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.parentOptionalGenerator() =
    "ParentOptionalGenerator" {
        title = "core/parentOptional"
        summary = "Nullable accessor generation on sealed parents, shared by `@ParentOptional` and `@ChildOptionals`"
        example("ParentOptionalAccessor")
        layout {
            KSP_MAIN_DIR / "core/parentOptional" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
