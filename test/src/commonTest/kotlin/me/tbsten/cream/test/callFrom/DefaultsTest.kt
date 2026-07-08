package me.tbsten.cream.test.callFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class DefaultsTest :
    FreeSpec({
        "マッチしないパラメータに元関数のデフォルトがあればブリッジから省かれ、元のデフォルトが適用される" {
            stamp(StampArgs("cream")) shouldBe "cream!"
        }

        "マッチするパラメータは元関数のデフォルトより source プロパティのコピーが優先される" {
            badge(BadgeArgs("cream", "?")) shouldBe "cream?"
        }

        "マッチするパラメータのコピーは呼び出し側の上書きでさらに上書きできる" {
            badge(BadgeArgs("cream", "?"), suffix = "#") shouldBe "cream#"
        }

        "@CallFrom.Exclude は元関数のデフォルトがあっても必須パラメータにする" {
            tally(TallyArgs("count", 5), count = 2) shouldBe "count:2"
        }
    })
