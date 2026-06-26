package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun overridingObject(
    name: String,
    parent: ClassName,
): TypeSpec =
    TypeSpec
        .objectBuilder(name)
        .addSuperinterface(parent)
        .addProperty(
            PropertySpec
                .builder("name", STRING)
                .addModifiers(OVERRIDE)
                .initializer("%S", "empty")
                .build(),
        ).build()

private fun objectHierarchy(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        abstractProps = listOf(Prop("name")),
        children =
            listOf(
                childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name"))),
                overridingObject("Empty", classNameOf("Source")),
            ),
    )

private fun nonDataClassHierarchy(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        abstractProps = listOf(Prop("name")),
        children =
            listOf(
                childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name"))),
                childClass("Frozen", classNameOf("Source"), overrides = listOf(Prop("name")), modifiers = emptyList()),
            ),
    )

private fun mixedNonCopyableHierarchy(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        abstractProps = listOf(Prop("name")),
        children =
            listOf(
                childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name"))),
                overridingObject("Empty", classNameOf("Source")),
                childClass("Frozen", classNameOf("Source"), overrides = listOf(Prop("name")), modifiers = emptyList()),
            ),
    )

internal fun nonCopyableStrategyScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "errorRejectsObject" to sealedCopy(objectHierarchy()),
        "returnAsIsObject" to sealedCopy(objectHierarchy(), nonCopyableStrategy = NonCopyableStrategy.RETURN_AS_IS),
        "returnNullObject" to sealedCopy(objectHierarchy(), nonCopyableStrategy = NonCopyableStrategy.RETURN_NULL),
        "nonDataClassRejected" to sealedCopy(nonDataClassHierarchy()),
        "mixedNonCopyableRejected" to sealedCopy(mixedNonCopyableHierarchy()),
    )
