package me.tbsten.cream.ksp.testing.smoke

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.generator
import me.tbsten.cream.ksp.testing.generator.toGenerator
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.combine
import me.tbsten.cream.ksp.testing.generator.util.combineToList
import me.tbsten.cream.ksp.testing.generator.util.list
import me.tbsten.cream.ksp.testing.generator.util.orNull
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Smoke test for the [generator] DSL and the combinators in `testing/generator/`. Verifies the
 * deterministic [Generator.representativeValues] side and that [Generator.arb] is usable; the
 * actual snapshot wiring (`<Feat>SnapshotTest`) comes later.
 */
internal class GeneratorSmokeTest :
    FreeSpec(
        {
            "generator DSL collects representative values (labelled + unlabelled) and exposes an Arb" {
                val names =
                    generator {
                        case("")
                        case("a")
                        "spaced" case "long name"
                        Arb.string()
                    }

                names.representativeValues().map { it.value }.toList() shouldBe listOf("", "a", "long name")
                names.representativeValues().last().label shouldBe "spaced"
                names.arb().take(5).count() shouldBe 5
            }

            "toGenerator wraps an existing Arb with explicit representative values" {
                val gen = Arb.string().toGenerator("x", "y")
                gen.representativeValues().map { it.value }.toList() shouldBe listOf("x", "y")
            }

            "orNull prepends a null representative value" {
                val gen = Arb.string().toGenerator("x").orNull()
                gen.representativeValues().map { it.value }.toList() shouldBe listOf(null, "x")
            }

            "Generator.list derives empty / single / all representatives" {
                val element = Arb.string().toGenerator("a", "b")
                val lists = Generator.list(element)
                lists.representativeValues().map { it.value }.toList() shouldBe
                    listOf(emptyList(), listOf("a"), listOf("a", "b"))
            }

            "cartesian merges paired labels with a custom combiner instead of the default \", \" join" {
                val left =
                    generator {
                        "L1" case "l1"
                        "L2" case "l2"
                        Arb.string()
                    }
                val right =
                    generator {
                        "R1" case "r1"
                        "R2" case "r2"
                        Arb.string()
                    }

                cartesian(left, right).representativeValues().first().label shouldBe "L1, R1"
                cartesian(left, right) { l, r -> "$l * $r" }
                    .representativeValues()
                    .map { it.label }
                    .toList() shouldBe listOf("L1 * R1", "L1 * R2", "L2 * R1", "L2 * R2")
            }

            "combineToList merges per-element labels with a custom combiner" {
                val a =
                    generator {
                        "A" case "a"
                        Arb.string()
                    }
                val b =
                    generator {
                        "B" case "b"
                        Arb.string()
                    }

                listOf(a, b)
                    .combineToList()
                    .representativeValues()
                    .first()
                    .label shouldBe "A, B"
                listOf(a, b)
                    .combineToList { labels -> labels.joinToString(" + ") }
                    .representativeValues()
                    .first()
                    .label shouldBe "A + B"
            }

            "cartesian has a 3-arg Triple version and combine scales to 5 / 6 generators" {
                val a = Arb.string().toGenerator("a1", "a2")
                val b = Arb.string().toGenerator("b1")
                val c = Arb.string().toGenerator("c1")
                val d = Arb.string().toGenerator("d1")
                val e = Arb.string().toGenerator("e1")
                val f = Arb.string().toGenerator("f1")

                cartesian(a, b, c).representativeValues().map { it.value }.toList() shouldBe
                    listOf(Triple("a1", "b1", "c1"), Triple("a2", "b1", "c1"))

                Generator
                    .combine(a, b, c, d, e) { v1, v2, v3, v4, v5 -> "$v1-$v2-$v3-$v4-$v5" }
                    .representativeValues()
                    .map { it.value }
                    .toList() shouldBe listOf("a1-b1-c1-d1-e1", "a2-b1-c1-d1-e1")

                Generator
                    .combine(a, b, c, d, e, f) { v1, v2, v3, v4, v5, v6 -> "$v1-$v2-$v3-$v4-$v5-$v6" }
                    .representativeValues()
                    .map { it.value }
                    .toList() shouldBe listOf("a1-b1-c1-d1-e1-f1", "a2-b1-c1-d1-e1-f1")
            }

            "union builder (re)labels members: prefix keeps sub-labels distinct, index-fallback for unlabelled, full transform" {
                val labelled =
                    generator {
                        "X" case "x"
                        "Y" case "y"
                        Arb.string()
                    }
                val unlabelled = Arb.string().toGenerator("u1", "u2") // toGenerator は label = null

                val u =
                    union {
                        "body" case labelled // -> "body/X", "body/Y"（潰れない）
                        "raw" case unlabelled // -> "raw[0]", "raw[1]"（index で一意）
                        case(labelled) // ラベルそのまま -> "X", "Y"
                        case(labelled) { it?.lowercase() } // フル変換 -> "x", "y"
                    }

                u.representativeValues().map { it.label }.toList() shouldBe
                    listOf("body/X", "body/Y", "raw[0]", "raw[1]", "X", "Y", "x", "y")
                u.representativeValues().map { it.value }.toList() shouldBe
                    listOf("x", "y", "u1", "u2", "x", "y", "x", "y")
                u.arb().take(5).count() shouldBe 5
            }

            "union withNumberPrefix auto-numbers cases (0-based) with configurable length / padChar / separator" {
                val labelled =
                    generator {
                        "X" case "x"
                        Arb.string()
                    }
                val unlabelled = Arb.string().toGenerator("a1") // label = null

                union {
                    withNumberPrefix(length = 2, padChar = '0', separator = ":") {
                        "first" case labelled // -> "00:first/X"
                        "second" case unlabelled // -> "01:second[0]"（unlabelled は index フォールバック）
                    }
                }.representativeValues()
                    .map { it.label }
                    .toList() shouldBe listOf("00:first/X", "01:second[0]")

                union {
                    withNumberPrefix(length = 3, padChar = '_', separator = "-") {
                        "only" case labelled // -> "__0-only/X"
                    }
                }.representativeValues()
                    .map { it.label }
                    .toList() shouldBe listOf("__0-only/X")
            }
        },
    )
