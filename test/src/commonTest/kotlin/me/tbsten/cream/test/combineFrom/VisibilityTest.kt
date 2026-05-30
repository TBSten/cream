package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VisibilityTest :
    FunSpec({
        // The generated function being `internal` is verified at compile time: this test
        // (in the same module) can call it. A `public`-only caller would not compile.
        test("internalVisibilityCombineFunctionIsCallable") {
            val source = InternalVisibilityCombineSource(shared = "value")
            val target: InternalVisibilityCombineTarget =
                source.copyToInternalVisibilityCombineTarget(extra = 42)

            target shouldBe InternalVisibilityCombineTarget("value", 42)
        }
    })
