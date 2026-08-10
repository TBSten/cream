package me.tbsten.cream.test.copyMapping

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ValueClassMappingTest :
    FreeSpec({
        "forwardMappingWrapsIntoValueClass" {
            val raw = RawIdModel(id = "id-1", name = "forward")

            val wrapped: WrappedIdModel = raw.copyToWrappedIdModel()

            wrapped shouldBe WrappedIdModel(id = MappedId("id-1"), name = "forward")
        }

        "reverseMappingUnwrapsFromValueClass" {
            val wrapped = WrappedIdModel(id = MappedId("id-2"), name = "reverse")

            val raw: RawIdModel = wrapped.copyToRawIdModel()

            raw shouldBe RawIdModel(id = "id-2", name = "reverse")
        }
    })
