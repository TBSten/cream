package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ObjectTargetTest :
    FunSpec({
        test("dataObjectToDataObject") {
            val source = EmptySource
            val target: EmptyTarget = source.copyToEmptyTarget()

            target shouldBe EmptyTarget
        }
    })
