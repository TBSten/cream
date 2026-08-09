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

        // 必須化そのものは「省略した呼び出しがコンパイルできない」性質で実行時には書けないため、
        // golden (09--exclude/excludedParameter) 側で固定している。
        "@CallFrom.Exclude されたパラメータは source プロパティを無視して呼び出し側の値を転送する" {
            tally(TallyArgs("count", 5), count = 2) shouldBe "count:2"
        }
    })
