package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class AnnotationClassTargetTest :
    FreeSpec({
        "dataClassToAnnotationClass" {
            val source = AnnotationClassSource(name = "hello", count = 3)

            val target: AnnotationClassTarget = source.copyToAnnotationClassTarget()

            target.name shouldBe "hello"
            target.count shouldBe 3
        }

        "dataClassToAnnotationClassWithOverride" {
            val source = AnnotationClassSource(name = "hello", count = 3)

            val target = source.copyToAnnotationClassTarget(count = 99)

            target.name shouldBe "hello"
            target.count shouldBe 99
        }
    })
