package me.tbsten.cream.test.copyMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExcludesTest :
    FreeSpec({
        "excludes drops the auto-default, so shareProp must be supplied explicitly" {
            val x = ExcludesLibX(shareProp = "a", xProp = 1)

            // `shareProp` lost its `= this.shareProp` default (excludes = ["shareProp"]), so it is required here.
            val y = x.copyToExcludesLibY(shareProp = "b", yProp = 2)

            y shouldBe ExcludesLibY(shareProp = "b", yProp = 2)
        }
    })
