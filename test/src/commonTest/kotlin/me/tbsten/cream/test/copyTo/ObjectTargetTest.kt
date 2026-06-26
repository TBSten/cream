package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ObjectTargetTest :
    FreeSpec({
        "dataObjectToDataObject" {
            val source = EmptySource
            val target: EmptyTarget = source.copyToEmptyTarget()

            target shouldBe EmptyTarget
        }
    })
