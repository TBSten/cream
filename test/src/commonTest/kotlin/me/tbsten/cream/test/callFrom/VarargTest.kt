package me.tbsten.cream.test.callFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class VarargTest :
    FreeSpec({
        "vararg パラメータの引数はそのまま元の関数に転送される" {
            joinPlain(JoinPlainArgs("unused"), "a", "b", "c") shouldBe "a+b+c"
        }

        "vararg 引数を渡さない場合は空の vararg として転送される" {
            joinPlain(JoinPlainArgs("unused")) shouldBe ""
        }

        "Array プロパティにマッチした vararg はデフォルト値として展開される" {
            joinAll(JoinAllArgs(arrayOf("x", "y"))) shouldBe "x+y"
        }

        "Array プロパティにマッチした vararg も個別引数で上書きできる" {
            joinAll(JoinAllArgs(arrayOf("x")), "a", "b") shouldBe "a+b"
        }
    })
