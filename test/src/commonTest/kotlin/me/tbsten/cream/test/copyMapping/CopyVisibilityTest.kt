package me.tbsten.cream.test.copyMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class CopyVisibilityTest :
    FreeSpec({
        // The `internal` visibility modifier itself is pinned by the snapshot tests (the golden
        // output shows the `internal` keyword on the generated functions); callability alone cannot
        // distinguish `internal` from `public`. This integration test instead covers the
        // bidirectional generation and runtime behaviour of a `canReverse = true` mapping: both the
        // forward and reverse functions are generated and produce the expected values.
        "internalForwardCopyFunctionIsCallable" {
            val source = InternalVisibilityLibA(shared = "value", aProp = 1)
            val target: InternalVisibilityLibB = source.copyToInternalVisibilityLibB(bProp = true)

            target shouldBe InternalVisibilityLibB("value", true)
        }

        "internalReverseCopyFunctionIsCallable" {
            val source = InternalVisibilityLibB(shared = "value", bProp = false)
            val target: InternalVisibilityLibA = source.copyToInternalVisibilityLibA(aProp = 7)

            target shouldBe InternalVisibilityLibA("value", 7)
        }
    })
