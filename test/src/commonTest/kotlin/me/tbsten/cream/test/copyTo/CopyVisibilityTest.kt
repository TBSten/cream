package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CopyVisibilityTest :
    FunSpec({
        // The generated function being `internal` is verified at compile time: this test
        // (in the same module) can call it. A `public`-only caller would not compile.
        test("internalVisibilityCopyFunctionIsCallable") {
            val source = InternalVisibilitySource(shared = "value")
            val target: InternalVisibilityTarget = source.copyToInternalVisibilityTarget(extra = 42)

            target shouldBe InternalVisibilityTarget("value", 42)
        }
    })
