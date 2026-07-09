package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ValueClassMappingMatrixTest :
    FreeSpec({
        "nullability" - {
            "wrapIntoNullableTargetUpcastsTheNonNullWrap" {
                WrapIntoNullableSource(id = "id-1").copyToWrapIntoNullableTarget() shouldBe
                    WrapIntoNullableTarget(id = AutoMappedId("id-1"))
            }

            "wrapFromNullableSourceUsesSafeCall" {
                WrapNullableSourceSource(id = "id-1").copyToWrapNullableSourceTarget() shouldBe
                    WrapNullableSourceTarget(id = AutoMappedId("id-1"))
            }

            "wrapFromNullableSourcePreservesNull" {
                WrapNullableSourceSource(id = null).copyToWrapNullableSourceTarget() shouldBe
                    WrapNullableSourceTarget(id = null)
            }

            "unwrapIntoNullableTargetUpcastsTheNonNullUnwrap" {
                UnwrapIntoNullableSource(id = AutoMappedId("id-2")).copyToUnwrapIntoNullableTarget() shouldBe
                    UnwrapIntoNullableTarget(id = "id-2")
            }

            "unwrapFromNullableSourceUsesSafeCall" {
                UnwrapNullableSourceSource(id = AutoMappedId("id-2")).copyToUnwrapNullableSourceTarget() shouldBe
                    UnwrapNullableSourceTarget(id = "id-2")
            }

            "unwrapFromNullableSourcePreservesNull" {
                UnwrapNullableSourceSource(id = null).copyToUnwrapNullableSourceTarget() shouldBe
                    UnwrapNullableSourceTarget(id = null)
            }
        }

        "nullableUnderlying" - {
            "wrapFeedsNullableSourceIntoNullableUnderlying" {
                NullableInnerWrapSource(id = null).copyToNullableInnerWrapTarget() shouldBe
                    NullableInnerWrapTarget(id = NullableInnerId(null))
            }

            "unwrapYieldsTheNullableUnderlying" {
                NullableInnerUnwrapSource(id = NullableInnerId("id-3")).copyToNullableInnerUnwrapTarget() shouldBe
                    NullableInnerUnwrapTarget(id = "id-3")
            }
        }

        "chainedValueClasses" - {
            "wrapNestsOneConstructorCallPerLayer" {
                ChainedWrapSource(id = "id-4").copyToChainedWrapTarget() shouldBe
                    ChainedWrapTarget(id = ChainedUserId(ChainedRawId("id-4")))
            }

            "unwrapReadsOneUnderlyingPropertyPerLayer" {
                ChainedUnwrapSource(id = ChainedUserId(ChainedRawId("id-4"))).copyToChainedUnwrapTarget() shouldBe
                    ChainedUnwrapTarget(id = "id-4")
            }

            "chainedWrapFromNullableSourcePreservesNull" {
                ChainedNullableWrapSource(id = null).copyToChainedNullableWrapTarget() shouldBe
                    ChainedNullableWrapTarget(id = null)
                ChainedNullableWrapSource(id = "id-4").copyToChainedNullableWrapTarget() shouldBe
                    ChainedNullableWrapTarget(id = ChainedUserId(ChainedRawId("id-4")))
            }
        }

        "typealiasResolution" - {
            "wrapThroughAliasedParameterType" {
                TypealiasWrapSource(id = "id-5").copyToTypealiasWrapTarget() shouldBe
                    TypealiasWrapTarget(id = AutoMappedId("id-5"))
            }

            "unwrapFromAliasedSourcePropertyType" {
                TypealiasUnwrapSource(id = AutoMappedId("id-5")).copyToTypealiasUnwrapTarget() shouldBe
                    TypealiasUnwrapTarget(id = "id-5")
            }
        }

        "mapInteraction" - {
            "mapRenamedPropertyAlsoWraps" {
                MapRenamedWrapSource(rawId = "id-6").copyToMapRenamedWrapTarget() shouldBe
                    MapRenamedWrapTarget(id = AutoMappedId("id-6"))
            }
        }

        "excludePrecedence" - {
            "excludedParameterStaysRequiredAndTakesTheExplicitValue" {
                val source = ExcludedWrapSource(id = "ignored", name = "keep")

                // `id = ...` is REQUIRED here: @CopyTo.Exclude suppressed the conversion default.
                val target = source.copyToExcludedWrapTarget(id = AutoMappedId("explicit"))

                target shouldBe ExcludedWrapTarget(id = AutoMappedId("explicit"), name = "keep")
            }
        }

        "constructorVisibility" - {
            "internalConstructorInTheSameModuleWraps" {
                InternalCtorWrapSource(id = "id-7").copyToInternalCtorWrapTarget() shouldBe
                    InternalCtorWrapTarget(id = InternalCtorId("id-7"))
            }
        }

        "directionDeterminism" - {
            "unwrapWinsWhenBothDirectionsApply" {
                val wrapper = AmbiguousWrapper(raw = "id-8")
                val source = UnwrapWinsSource(item = AmbiguousHolder(wrapper = wrapper))

                // Wrap would have produced AmbiguousWrapper(raw = AmbiguousHolder(...)) instead.
                source.copyToUnwrapWinsTarget() shouldBe UnwrapWinsTarget(item = wrapper)
            }
        }
    })
