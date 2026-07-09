package me.tbsten.cream.test.callFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FreeSpec({
        "args のプロパティが引数に展開されて元の関数が呼ばれる" {
            processData(ProcessDataArgs("a", 1)) shouldBe "a-1"
        }

        "個別引数を渡すと args のプロパティより優先される" {
            processData(ProcessDataArgs("a", 1), data2 = 9) shouldBe "a-9"
        }

        "source のプロパティにマッチしないパラメータは必須引数として残る" {
            greet(GreetArgs("cream"), punctuation = "!") shouldBe "Hello, cream!"
        }

        "suspend 関数のブリッジも suspend 関数として生成される" {
            load(LoadArgs("abc")) shouldBe 3
        }

        "member 関数のブリッジは enclosing クラスの拡張関数として呼べる" {
            Calculator(3).multiply(MultiplyArgs(5)) shouldBe 15
        }
    })
