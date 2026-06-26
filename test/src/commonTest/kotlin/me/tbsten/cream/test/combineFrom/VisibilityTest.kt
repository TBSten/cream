package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class VisibilityTest :
    FreeSpec({
        // The generated function being `internal` is verified at compile time: this test
        // (in the same module) can call it. A `public`-only caller would not compile.
        "internalVisibilityCombineFunctionIsCallable" {
            val source = InternalVisibilityCombineSource(shared = "value")
            val target: InternalVisibilityCombineTarget =
                source.copyToInternalVisibilityCombineTarget(extra = 42)

            target shouldBe InternalVisibilityCombineTarget("value", 42)
        }
    })
