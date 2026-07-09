package me.tbsten.cream.test.callFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExtensionTest :
    FreeSpec({
        "拡張関数のブリッジは同じレシーバの拡張関数として呼べる" {
            "Hello".greetWith(GreetExtArgs("cream")) shouldBe "Hello, cream"
        }

        "拡張関数のブリッジでも個別引数の上書きが効く" {
            "Hello".greetWith(GreetExtArgs("cream"), name = "world") shouldBe "Hello, world"
        }

        "generic なレシーバの型パラメータがブリッジに転写される" {
            listOf("a", "b", "c").pickAt(PickArgs(1)) shouldBe "b"
        }

        "nullable レシーバ + suspend の拡張関数もブリッジできる" {
            val absent: String? = null
            absent.orValue(OrValueArgs("fallback")) shouldBe "fallback"
            val present: String? = "value"
            present.orValue(OrValueArgs("fallback")) shouldBe "value"
        }
    })
