package me.tbsten.cream.test.copyFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ValueClassMappingTest :
    FreeSpec({
        "wrapGeneratesValueClassConstructorCallDefault" {
            val source = ValueClassWrapSource(id = "id-1", name = "wrap")

            val target = source.copyToValueClassWrapTarget()

            target shouldBe ValueClassWrapTarget(id = AutoMappedId("id-1"), name = "wrap")
        }

        "unwrapGeneratesUnderlyingPropertyAccessDefault" {
            val source = ValueClassUnwrapSource(id = AutoMappedId("id-2"), name = "unwrap")

            val target = source.copyToValueClassUnwrapTarget()

            target shouldBe ValueClassUnwrapTarget(id = "id-2", name = "unwrap")
        }
    })
