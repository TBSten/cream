package me.tbsten.cream.ksp.testing.poet

import com.squareup.kotlinpoet.TypeSpec
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE

internal class SnapshotScenarioTest :
    FreeSpec({
        fun clazz(name: String) = TypeSpec.classBuilder(name).build()

        "vararg 宣言は単一パッケージ・単一ファイルになり先頭宣言の FQN で命名される" {
            val scenario = SnapshotScenario(clazz("Source"), clazz("Target"))

            assertSoftly {
                scenario.files.size shouldBe 1
                scenario.files[0].packageName shouldBe GENERATED_PACKAGE
                // ファイル名（= Input: facet 名）は先頭宣言の完全修飾名にする。
                scenario.files[0].name shouldBe "$GENERATED_PACKAGE.Source"
                scenario.files[0].toString() shouldContain "class Source"
                scenario.files[0].toString() shouldContain "class Target"
            }
        }

        "inputFileSpec を複数渡すとパッケージごとに別ファイルになり各先頭宣言の FQN で命名される" {
            val scenario =
                SnapshotScenario(
                    files =
                        listOf(
                            inputFileSpec("com.example.lib", clazz("LibSource")),
                            inputFileSpec("com.example.mapping", clazz("Mapping")),
                        ),
                )

            assertSoftly {
                scenario.files.size shouldBe 2
                scenario.files[0].packageName shouldBe "com.example.lib"
                scenario.files[0].name shouldBe "com.example.lib.LibSource"
                scenario.files[0].toString() shouldContain "class LibSource"
                scenario.files[1].packageName shouldBe "com.example.mapping"
                scenario.files[1].name shouldBe "com.example.mapping.Mapping"
                scenario.files[1].toString() shouldContain "class Mapping"
            }
        }
    })
