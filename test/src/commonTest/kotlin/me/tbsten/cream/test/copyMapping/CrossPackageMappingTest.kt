package me.tbsten.cream.test.copyMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.test.copyMapping.lib.CrossSource
import me.tbsten.cream.test.copyMapping.lib.CrossTarget

class CrossPackageMappingTest :
    FreeSpec({
        // The generated extension is in scope here without an explicit import: issue #145 emits it
        // into this (the holder's) package, not into the source class's `...copyMapping.lib` package.
        // If it were still generated into the source package this file would fail to compile.
        "crossPackageMappingGeneratesIntoHolderPackage" {
            val source = CrossSource(shared = "shared", sourceOnly = 1)

            val target: CrossTarget = source.copyToCrossTarget(targetOnly = 7)

            target shouldBe CrossTarget(shared = "shared", targetOnly = 7)
        }
    })
