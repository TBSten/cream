package me.tbsten.cream.test.architecture.creamKsp.core.coreCommon

import me.tbsten.cream.test.architecture.support.KSP_MAIN_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.coreCommon() =
    "CoreCommon" {
        title = "core/common"
        summary = "Building blocks shared by every generator: type parameters, property matching, KDoc, naming and `GenerateSourceAnnotation`"
        example("GenerateSourceAnnotation")
        example("FindMatchedProperty")
        layout {
            KSP_MAIN_DIR / "core/common" / "*".ktFile()
        }
        packageFollowsDirectory()
    }
