package me.tbsten.cream.test.callFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ModifiersTest :
    FreeSpec({
        "operator fun のブリッジは通常の関数として呼べる（operator は転写されない）" {
            Counter(1).plus(AddArgs(2)).value shouldBe 3
        }

        "infix fun のブリッジは通常の関数として呼べる（infix は転写されない）" {
            Joiner("a").join(JoinWithArgs("b")) shouldBe "ab"
        }

        "inline fun のブリッジはラムダ引数をそのまま転送できる" {
            runTagged(RunTaggedArgs("tag:")) { "body" } shouldBe "tag:body"
        }

        "tailrec fun のブリッジは元関数の再帰をそのまま呼ぶ" {
            countDown(CountDownArgs(5)) shouldBe 0
        }
    })
