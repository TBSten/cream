package me.tbsten.cream.test.combineTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs

class ObjectTargetTest :
    FreeSpec({
        "combineToObject" {
            val sourceA = ObjectSourceA(propertyA = "test")
            val sourceB = ObjectSourceB(propertyB = 42)

            val result =
                sourceA.copyToTargetObject(
                    objectSourceB = sourceB,
                )

            // Objects are singletons, so the result should be the same instance
            result shouldBeSameInstanceAs TargetObject
        }
    })
