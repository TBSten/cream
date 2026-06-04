package me.tbsten.cream.test.copyMapping

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TypeAliasTest :
    FunSpec({
        test("copyMappingResolvesTypeAliasSourceAndTarget") {
            // Source/target are declared via type aliases (LibAliasASource / LibAliasBTarget),
            // but the generated function is named after the resolved class (LibAliasBModel).
            val source: LibAliasASource = LibAliasAModel(shareProp = "shared", aProp = 7)

            val result: LibAliasBTarget = source.copyToLibAliasBModel(bProp = 100)

            assertSoftly {
                result.shareProp shouldBe "shared"
                result.bProp shouldBe 100
                result.shouldBeInstanceOf<LibAliasBModel>()
            }
        }

        test("copyMappingTypeAliasAllowsOverride") {
            val source: LibAliasASource = LibAliasAModel(shareProp = "shared", aProp = 7)

            val result = source.copyToLibAliasBModel(shareProp = "overridden", bProp = 100)

            val expected = LibAliasBModel(shareProp = "overridden", bProp = 100)

            result shouldBe expected
        }
    })
