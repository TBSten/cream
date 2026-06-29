package me.tbsten.cream.ksp.testing.poet

import com.squareup.kotlinpoet.TypeSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE

internal class SnapshotScenarioTest :
    FreeSpec({
        fun clazz(name: String) = TypeSpec.classBuilder(name).build()

        "vararg 宣言は単一パッケージ・単一ファイルになる" {
            val scenario = SnapshotScenario(clazz("Source"), clazz("Target"))

            val files = scenario.toFileSpecs()

            files.size shouldBe 1
            files[0].packageName shouldBe GENERATED_PACKAGE
            // 既存 golden を壊さないため単一ファイル時の名前は連番なしの "Input" に固定する。
            files[0].name shouldBe "Input"
            files[0].toString() shouldContain "class Source"
            files[0].toString() shouldContain "class Target"
        }

        "ScenarioFile を複数渡すとパッケージごとに別ファイルへ連番付きで分かれる" {
            val scenario =
                SnapshotScenario(
                    files =
                        listOf(
                            ScenarioFile("com.example.lib", clazz("LibSource")),
                            ScenarioFile("com.example.mapping", clazz("Mapping")),
                        ),
                )

            val files = scenario.toFileSpecs()

            files.size shouldBe 2
            files[0].packageName shouldBe "com.example.lib"
            files[0].name shouldBe "Input1"
            files[0].toString() shouldContain "class LibSource"
            files[1].packageName shouldBe "com.example.mapping"
            files[1].name shouldBe "Input2"
            files[1].toString() shouldContain "class Mapping"
        }
    })
