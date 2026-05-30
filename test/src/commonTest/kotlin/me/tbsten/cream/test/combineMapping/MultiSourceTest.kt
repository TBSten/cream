package me.tbsten.cream.test.combineMapping

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MultiSourceTest :
    FunSpec({
        test("tripleSourceCombineMapping") {
            val libA =
                LibAModel(
                    nameA = "A",
                    valueA = 1,
                )
            val libB =
                LibBModel(
                    nameB = "B",
                    valueB = 2.0,
                )
            val libC =
                LibCModel(
                    nameC = "C",
                    valueC = true,
                )

            val result: TripleCombinedModel =
                libA.copyToTripleCombinedModel(
                    libBModel = libB,
                    libCModel = libC,
                )

            val expected =
                TripleCombinedModel(
                    nameA = "A",
                    valueA = 1,
                    nameB = "B",
                    valueB = 2.0,
                    nameC = "C",
                    valueC = true,
                )

            result shouldBe expected
        }

        test("tripleSourceCombineMappingWithOverride") {
            val libA =
                LibAModel(
                    nameA = "A",
                    valueA = 1,
                )
            val libB =
                LibBModel(
                    nameB = "B",
                    valueB = 2.0,
                )
            val libC =
                LibCModel(
                    nameC = "C",
                    valueC = true,
                )

            val result: TripleCombinedModel =
                libA.copyToTripleCombinedModel(
                    libBModel = libB,
                    libCModel = libC,
                    nameB = "OverriddenB",
                    valueC = false,
                )

            val expected =
                TripleCombinedModel(
                    nameA = "A",
                    valueA = 1,
                    nameB = "OverriddenB",
                    valueB = 2.0,
                    nameC = "C",
                    valueC = false,
                )

            result shouldBe expected
        }
    })
