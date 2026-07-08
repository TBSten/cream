package me.tbsten.cream.test.callFrom

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ShapesTest :
    FreeSpec({
        "companion object のメンバ関数は Companion への拡張関数としてブリッジされる" {
            Factory.make(MakeArgs("v")) shouldBe "made:v"
        }

        "typealias されたプロパティ型は実体の型とマッチする" {
            resolve(ResolveArgs("u1")) shouldBe "resolved:u1"
        }

        "Nothing 戻り値のブリッジは元関数と同じく例外を投げる" {
            shouldThrow<IllegalStateException> {
                failWith(FailArgs("boom"))
            }.message shouldBe "boom"
        }

        "nullable 戻り値が保持される" {
            find(FindArgs("")) shouldBe null
            find(FindArgs("id-1")) shouldBe "id-1"
        }

        "@Deprecated(WARNING) な関数のブリッジには @Deprecated が伝播しつつ呼び出せる" {
            @Suppress("DEPRECATION")
            legacyProcess(LegacyArgs("v")) shouldBe "legacy:v"
        }
    })
