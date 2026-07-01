package me.tbsten.cream.test.combineMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExcludesTest :
    FreeSpec({
        "excludes drops the auto-default, so propA must be supplied explicitly" {
            val a = ExcludesLibA(propA = "a", valueA = 1)
            val b = ExcludesLibB(propB = "b", valueB = 2.0)

            // `propA` lost its `= this.propA` default (excludes = ["propA"]); the other matched props keep theirs.
            val combined = a.copyToExcludesCombinedModel(excludesLibB = b, propA = "x")

            combined shouldBe ExcludesCombinedModel(propA = "x", valueA = 1, propB = "b", valueB = 2.0)
        }
    })
