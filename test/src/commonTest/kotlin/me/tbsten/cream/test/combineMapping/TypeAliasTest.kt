package me.tbsten.cream.test.combineMapping

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TypeAliasTest :
    FunSpec({
        test("combineMappingResolvesTypeAliasSourcesAndTarget") {
            // sources and target are declared via type aliases, but the generated function
            // is named after / typed by the resolved classes.
            val first: LibAliasFirstSource = LibAliasFirstModel(firstName = "First", firstValue = 10)
            val second: LibAliasSecondSource = LibAliasSecondModel(secondName = "Second", secondValue = 2.5)

            val result: LibAliasCombinedTarget =
                first.copyToLibAliasCombinedModel(
                    libAliasSecondModel = second,
                    extraProperty = "Extra",
                )

            assertSoftly {
                result.firstName shouldBe "First"
                result.firstValue shouldBe 10
                result.secondName shouldBe "Second"
                result.secondValue shouldBe 2.5
                result.extraProperty shouldBe "Extra"
                result.shouldBeInstanceOf<LibAliasCombinedModel>()
            }
        }

        test("combineMappingTypeAliasAllowsOverride") {
            val first: LibAliasFirstSource = LibAliasFirstModel(firstName = "First", firstValue = 10)
            val second: LibAliasSecondSource = LibAliasSecondModel(secondName = "Second", secondValue = 2.5)

            val result =
                first.copyToLibAliasCombinedModel(
                    libAliasSecondModel = second,
                    firstName = "OverriddenFirst",
                    secondValue = 9.99,
                    extraProperty = "Extra",
                )

            val expected =
                LibAliasCombinedModel(
                    firstName = "OverriddenFirst",
                    firstValue = 10,
                    secondName = "Second",
                    secondValue = 9.99,
                    extraProperty = "Extra",
                )

            result shouldBe expected
        }
    })
