package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * Children in a *subtype relation* merged into one accessor:
 * `sealed class Middle(@ParentOptional(propertyName = "acc") val x)` with
 * `class Leaf(x, @ParentOptional(propertyName = "acc") val y) : Middle(x)`. The generated
 * `when` must dispatch `is Leaf` *before* `is Middle`, or the `Middle` branch would shadow
 * `Leaf` and return `x` (wrong value) for `Leaf` instances. Declaration order intentionally
 * puts `Middle` first so discovery order alone would emit the shadowed (buggy) order.
 */
private fun subtypeChildrenHierarchy(): List<TypeSpec> {
    val source = TypeSpec.interfaceBuilder("Source").addModifiers(SEALED).build()
    val middle =
        TypeSpec
            .classBuilder("Middle")
            .addModifiers(SEALED)
            .addSuperinterface(classNameOf("Source"))
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter(
                        ParameterSpec
                            .builder("x", STRING)
                            .addAnnotation(parentOptional(propertyName = "acc"))
                            .build(),
                    ).build(),
            ).addProperty(PropertySpec.builder("x", STRING).initializer("x").build())
            .build()
    val leaf =
        TypeSpec
            .classBuilder("Leaf")
            .superclass(classNameOf("Middle"))
            .addSuperclassConstructorParameter("x")
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter("x", STRING)
                    .addParameter(
                        ParameterSpec
                            .builder("y", STRING)
                            .addAnnotation(parentOptional(propertyName = "acc"))
                            .build(),
                    ).build(),
            ).addProperty(PropertySpec.builder("y", STRING).initializer("y").build())
            .build()
    return listOf(source, middle, leaf)
}

/**
 * Diamond-ish hierarchy: `Leaf` implements TWO sealed interfaces (`MidA`, `MidB`), both of which
 * declare a `@ParentOptional` abstract `val x` and both extend `Root`. The `Root` accessor merges
 * the two *unrelated* contributions (`is MidA` / `is MidB` in discovery order — neither shadows
 * the other, and a `Leaf` matches the first branch); `Leaf`'s single override serves both.
 */
private fun diamondHierarchy(): List<TypeSpec> {
    val root = TypeSpec.interfaceBuilder("Root").addModifiers(SEALED).build()

    fun mid(name: String): TypeSpec =
        TypeSpec
            .interfaceBuilder(name)
            .addModifiers(SEALED)
            .addSuperinterface(classNameOf("Root"))
            .addProperty(
                PropertySpec
                    .builder("x", STRING)
                    .addAnnotation(parentOptional())
                    .build(),
            ).build()
    val leaf =
        TypeSpec
            .classBuilder("Leaf")
            .addSuperinterface(classNameOf("MidA"))
            .addSuperinterface(classNameOf("MidB"))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("x", STRING).build())
            .addProperty(
                PropertySpec
                    .builder("x", STRING)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("x")
                    .build(),
            ).build()
    return listOf(root, mid("MidA"), mid("MidB"), leaf)
}

internal fun mergeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "twoChildrenMerged" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(parentOptionalProp("message"))),
                            childClass("Failure", classNameOf("Source"), props = listOf(parentOptionalProp("message"))),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        "mergeAcrossIntermediateAncestors" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Root",
                    children =
                        listOf(
                            childClass("Direct", classNameOf("Root"), props = listOf(parentOptionalProp("id"))),
                            nestedSealed(
                                "Middle",
                                classNameOf("Root"),
                                childClass("Leaf", classNameOf("Root", "Middle"), props = listOf(parentOptionalProp("id"))),
                            ),
                        ),
                ),
            ),
        "subtypeChildrenMostDerivedFirst" to SnapshotScenario(subtypeChildrenHierarchy()),
        "diamondChildOfTwoSealedInterfaces" to SnapshotScenario(diamondHierarchy()),
    )
