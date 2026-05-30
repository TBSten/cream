package me.tbsten.cream.test.combineFrom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs

class ObjectTargetTest :
    FunSpec({
        test("combineFromToObject") {
            val sourceA = ObjectSourceA(propertyA = "test")
            val sourceB = ObjectSourceB(propertyB = 42)

            val result: TargetObject =
                sourceA.copyToTargetObject(
                    objectSourceB = sourceB,
                )

            // Objects are singletons, so the result should be the same instance
            result shouldBeSameInstanceAs TargetObject
        }
    })
