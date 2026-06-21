package me.tbsten.cream.ksp.testing.smoke

import com.squareup.kotlinpoet.CHAR_SEQUENCE
import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.arbitrary.take
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.clazz.TypeKind
import me.tbsten.cream.ksp.testing.generator.clazz.asPrimaryConstructor
import me.tbsten.cream.ksp.testing.generator.clazz.basicType
import me.tbsten.cream.ksp.testing.generator.clazz.classSpec
import me.tbsten.cream.ksp.testing.generator.clazz.dataClassSpec
import me.tbsten.cream.ksp.testing.generator.clazz.enumSpec
import me.tbsten.cream.ksp.testing.generator.clazz.function
import me.tbsten.cream.ksp.testing.generator.clazz.objectSpec
import me.tbsten.cream.ksp.testing.generator.clazz.properties
import me.tbsten.cream.ksp.testing.generator.clazz.property
import me.tbsten.cream.ksp.testing.generator.clazz.toKotlinSource
import me.tbsten.cream.ksp.testing.generator.clazz.toSource
import me.tbsten.cream.ksp.testing.generator.clazz.typeSpec
import me.tbsten.cream.ksp.testing.generator.util.combineToList
import me.tbsten.cream.ksp.testing.generator.util.constant
import io.kotest.core.spec.style.FunSpec as KotestFunSpec

/**
 * Smoke test for the KotlinPoet-backed code generators in `testing/generator/clazz/`. Verifies the
 * deterministic [Generator.representativeValues] side renders compilable-looking Kotlin and that the
 * [Generator.arb] side is usable.
 */
internal class ClazzGeneratorSmokeTest :
    KotestFunSpec(
        {
            test("basicType generator exposes resolvable representative type names") {
                val labels =
                    Generator
                        .basicType()
                        .representativeValues()
                        .map { it.label }
                        .toList()
                labels shouldBe listOf("String", "Int", "Boolean", "String?", "List<String>")
            }

            test("properties generator yields common property lists") {
                val labels =
                    Generator
                        .properties()
                        .representativeValues()
                        .map { it.label }
                        .toList()
                labels shouldBe
                    listOf(
                        "Primitive and basic types",
                        "Unsigned and special types",
                        "Collection types",
                        "Generic types",
                        "Nullable types",
                        "Function types",
                    )
                Generator
                    .properties()
                    .representativeValues()
                    .first()
                    .value
                    .map { it.name } shouldBe listOf("int", "long", "float", "double", "boolean", "str", "byte", "short", "char")
            }

            test("dataClassSpec renders a data class from the default property lists") {
                val sources =
                    Generator
                        .dataClassSpec(name = Generator.constant("Sample"))
                        .toSource()
                        .representativeValues()
                        .map { it.value }
                        .toList()

                sources.size shouldBe 6 // properties() representative count
                val first = sources.first()
                first shouldContain "data class Sample"
                first shouldContain "int: Int"
                first shouldContain "str: String"
            }

            test("classSpec emits primary and secondary constructors") {
                val source =
                    Generator
                        .classSpec(
                            name = Generator.constant("Sample"),
                            constructors =
                                Generator.constant(
                                    listOf(
                                        FunSpec.constructorBuilder().addParameter("a", STRING).build(), // primary (a)
                                        FunSpec
                                            .constructorBuilder()
                                            .addParameter("a", STRING)
                                            .addParameter("b", STRING)
                                            .build(), // secondary (a, b)
                                    ),
                                ),
                        ).representativeValues()
                        .first()
                        .value
                        .toKotlinSource()
                source shouldContain "class Sample"
                source shouldContain "constructor("
                source shouldContain ": this(TODO())"
            }

            test("classSpec properties become body vals, distinct from constructor params") {
                val source =
                    Generator
                        .classSpec(
                            name = Generator.constant("Sample"),
                            constructors = listOf(Generator.property("a")).combineToList().asPrimaryConstructor(),
                            properties = listOf(Generator.property("b")).combineToList(),
                        ).representativeValues()
                        .first()
                        .value
                        .toKotlinSource()
                source shouldContain "val a: String" // primary constructor val
                source shouldContain "val b: String = TODO()" // body property
            }

            test("enumSpec shares the constructor / body-property model with classSpec") {
                val source =
                    Generator
                        .enumSpec(
                            name = Generator.constant("Color"),
                            constructors = listOf(Generator.property("rgb")).combineToList().asPrimaryConstructor(),
                            properties = listOf(Generator.property("label")).combineToList(),
                        ).representativeValues()
                        .first()
                        .value
                        .toKotlinSource()
                source shouldContain "enum class Color"
                source shouldContain "val rgb: String" // primary constructor val
                source shouldContain "val label: String = TODO()" // body property
                source shouldContain "A(TODO())" // constant passes a ctor arg
            }

            test("objectSpec renders an object with member functions") {
                val source =
                    Generator
                        .objectSpec(
                            name = Generator.constant("Singleton"),
                            properties = listOf(Generator.property("prop")).combineToList(),
                            functions = listOf(Generator.function(name = "method")),
                        ).representativeValues()
                        .first()
                        .value
                        .toKotlinSource()
                source shouldContain "object Singleton"
                source shouldContain "fun method"
            }

            test("classSpec yields a built TypeSpec that can still be post-processed via toBuilder") {
                val type =
                    Generator
                        .classSpec(name = Generator.constant("Foo"))
                        .representativeValues()
                        .first()
                        .value

                val source =
                    type
                        .toBuilder()
                        .addModifiers(KModifier.DATA)
                        .addFunction(FunSpec.builder("extra").addStatement("TODO()").build())
                        .build()
                        .toKotlinSource()
                source shouldContain "data class Foo"
                source shouldContain "fun extra"
            }

            test("typeSpec yields a class per (kind x property variation)") {
                val reps =
                    Generator
                        .typeSpec(name = Generator.constant("Sample"))
                        .representativeValues()
                        .toList()

                reps.size shouldBe 30
                reps.any { it.label == "class, Primitive and basic types" } shouldBe true
                reps.any { it.label == "object, Function types" } shouldBe true
                reps.any { it.label == "class, Single type parameter" } shouldBe true
                reps.any { it.label == "interface, Two type parameters" } shouldBe true
                reps.any { it.label == "class, Multiple bounds" } shouldBe true
                reps.count { it.label!!.startsWith("class, ") } shouldBe 9
                reps.count { it.label!!.startsWith("interface, ") } shouldBe 9
                reps.count { it.label!!.startsWith("enum, ") } shouldBe 6
                reps.count { it.label!!.startsWith("object, ") } shouldBe 6
            }

            test("typeSpec renders generic class / interface declarations") {
                val sources =
                    Generator
                        .typeSpec(name = Generator.constant("Sample"), kinds = listOf(TypeKind.Class))
                        .toSource()
                        .representativeValues()
                        .map { it.value }
                        .toList()
                sources.any { it.contains("public class Sample<T>") } shouldBe true
                sources.any { it.contains("public class Sample<K, V>") } shouldBe true
            }

            test("classSpec derives multi-bound type parameters into a where clause") {
                val t = TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T")), CHAR_SEQUENCE)
                val source =
                    Generator
                        .classSpec(
                            name = Generator.constant("Sample"),
                            constructors =
                                listOf(Generator.property("current", Generator.constant<TypeName>(t)))
                                    .combineToList()
                                    .asPrimaryConstructor(),
                        ).representativeValues()
                        .first()
                        .value
                        .toKotlinSource()
                source shouldContain "class Sample<T>"
                source shouldContain "val current: T"
                source shouldContain "where T : Comparable<T>, T : CharSequence"
            }

            test("classSpec exposes an Arb usable for property tests") {
                val gen = Generator.classSpec(name = Generator.constant("Sample"))
                gen.arb().take(5).count() shouldBe 5
            }
        },
    )
