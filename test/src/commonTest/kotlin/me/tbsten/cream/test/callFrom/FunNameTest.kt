package me.tbsten.cream.test.callFrom

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FunNameTest :
    FreeSpec({
        "custom funName で生成されたブリッジは元関数へ委譲して呼び出せる" {
            createBuildConfig(BuildConfigArgs("cream", 3)) shouldBe "cream:3"
        }

        "custom funName ブリッジでも個別引数で source プロパティを上書きできる" {
            createBuildConfig(BuildConfigArgs("cream", 3), size = 9) shouldBe "cream:9"
        }

        "member 関数の custom funName ブリッジは receiver 経由で元メンバーへ委譲する" {
            Renderer(2).scaledBy(ScaleArgs(5)) shouldBe 10
        }
    })
