package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class CopyVisibilityTest :
    FreeSpec({
        // The generated function being `internal` is verified at compile time: this test
        // (in the same module) can call it. A `public`-only caller would not compile.
        "internalVisibilityCopyFunctionIsCallable" {
            val source = InternalVisibilitySource(shared = "value")
            val target: InternalVisibilityTarget = source.copyToInternalVisibilityTarget(extra = 42)

            target shouldBe InternalVisibilityTarget("value", 42)
        }
    })
