package me.tbsten.cream.test.vararg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VarargTest :
    FunSpec({
        test("copies a vararg ctor param from the source without an override") {
            val source = VarargSource(id = 1, "a", "b", "c")

            val target = source.copyToVarargTarget()

            target.id shouldBe 1
            target.tags.toList() shouldBe listOf("a", "b", "c")
        }

        test("overriding a vararg param replaces it while id is carried over from the source") {
            val source = VarargSource(id = 7, "a", "b", "c")

            val target = source.copyToVarargTarget(tags = arrayOf("x", "y"))

            target.id shouldBe 7
            target.tags.toList() shouldBe listOf("x", "y")
        }

        test("copies a primitive vararg ctor param from the source") {
            val source = PrimitiveVarargSource(id = 2, 10, 20, 30)

            val target = source.copyToPrimitiveVarargTarget()

            target.id shouldBe 2
            target.nums.toList() shouldBe listOf(10, 20, 30)
        }

        test("copies an Array source property into a vararg target param") {
            val source = ArrayToVarargSource(id = 3, arrayOf("a", "b"))

            val target = source.copyToArrayToVarargTarget()

            target.id shouldBe 3
            target.tags.toList() shouldBe listOf("a", "b")
        }
    })
