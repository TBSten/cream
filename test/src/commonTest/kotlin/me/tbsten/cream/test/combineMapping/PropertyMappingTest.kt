package me.tbsten.cream.test.combineMapping

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PropertyMappingTest :
    FunSpec({
        test("combineMappingWithPropertyNames") {
            val libA =
                LibAModel(
                    nameA = "SourceNameA",
                    valueA = 42,
                )
            val libB =
                LibBModel(
                    nameB = "SourceNameB",
                    valueB = 2.71,
                )

            val result: RenamedTargetModel =
                libA.copyToRenamedTargetModel(
                    libBModel = libB,
                    extra = "Extra",
                )

            val expected =
                RenamedTargetModel(
                    targetNameA = "SourceNameA",
                    targetValueA = 42,
                    targetNameB = "SourceNameB",
                    targetValueB = 2.71,
                    extra = "Extra",
                )

            result shouldBe expected
        }

        test("combineMappingWithPropertyNamesAndOverride") {
            val libA =
                LibAModel(
                    nameA = "SourceNameA",
                    valueA = 42,
                )
            val libB =
                LibBModel(
                    nameB = "SourceNameB",
                    valueB = 2.71,
                )

            val result: RenamedTargetModel =
                libA.copyToRenamedTargetModel(
                    libBModel = libB,
                    targetNameA = "Overridden",
                    targetValueB = 999.0,
                    extra = "Extra",
                )

            val expected =
                RenamedTargetModel(
                    targetNameA = "Overridden",
                    targetValueA = 42,
                    targetNameB = "SourceNameB",
                    targetValueB = 999.0,
                    extra = "Extra",
                )

            result shouldBe expected
        }
    })
