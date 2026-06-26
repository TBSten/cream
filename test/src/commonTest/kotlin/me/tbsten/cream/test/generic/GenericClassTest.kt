package me.tbsten.cream.test.generic

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class GenericClassTest :
    FreeSpec({
        "twoArgToThreeArg" {
            val source =
                GenericSourceWithTwoTypeArg(
                    a = "test",
                    b = listOf("a", "b"),
                    d = 123,
                )

            mapOf(
                // FIXME
                // https://github.com/TBSten/cream/issues/12
                // Default value cannot be set correctly in a copy function using type parameters.
                // KSType.isAssignableFrom(KSType) in FindMatchedProperty.kt does not seem to work.
//            source.copyToGenericTargetWithThreeTypeArg(
//                c = "c",
//            ) to GenericTargetWithThreeTypeArg(
//                a = source.a,
//                b = source.b,
//                c = "c",
//            ),
                source.copyToGenericTargetWithThreeTypeArg(
                    a = "aaa",
                    b = listOf("bbb", "bbb", "bbb"),
                    c = "c",
                    d = 456,
                ) to
                    GenericTargetWithThreeTypeArg(
                        a = "aaa",
                        b = listOf("bbb", "bbb", "bbb"),
                        c = "c",
                        d = 456,
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        "threeArgToTwoArg" {
            val source =
                GenericSourceWithThreeTypeArg(
                    a = "test",
                    b = listOf("a", "b"),
                    c = "c",
                )

            mapOf(
                // FIXME
                // https://github.com/TBSten/cream/issues/12
                // Default value cannot be set correctly in a copy function using type parameters.
                // KSType.isAssignableFrom(KSType) in FindMatchedProperty.kt does not seem to work.
//            source.copyToGenericTargetWithTwoTypeArg(
//                a = source.a,
//                b = source.b,
//            ) to GenericTargetWithTwoTypeArg(
//                a = source.a,
//                b = source.b,
//            ),
                source.copyToGenericTargetWithTwoTypeArg(
                    a = "aaa",
                    b = listOf("bbb", "bbb", "bbb"),
                ) to
                    GenericTargetWithTwoTypeArg(
                        a = "aaa",
                        b = listOf("bbb", "bbb", "bbb"),
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        "threeArgToObject" {
            val source =
                GenericSourceWithThreeTypeArg(
                    a = "test",
                    b = listOf("a", "b"),
                    c = "c",
                )

            mapOf(
                source.copyToGenericTargetObject() to GenericTargetObject,
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }
    })
