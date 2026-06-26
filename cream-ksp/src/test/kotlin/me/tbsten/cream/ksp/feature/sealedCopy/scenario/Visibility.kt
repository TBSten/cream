package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun internalSealedClassParent(): TypeSpec {
    val child =
        TypeSpec
            .classBuilder("Child")
            .addModifiers(DATA)
            .superclass(classNameOf("Source"))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("name", STRING).build())
            .addProperty(
                PropertySpec
                    .builder("name", STRING)
                    .addModifiers(OVERRIDE)
                    .initializer("name")
                    .build(),
            ).build()
    return TypeSpec
        .classBuilder("Source")
        .addModifiers(INTERNAL, SEALED)
        .addProperty(PropertySpec.builder("name", STRING).addModifiers(ABSTRACT).build())
        .addType(child)
        .build()
}

private fun publicParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        abstractProps = listOf(Prop("name")),
        children = listOf(childClass("Child", classNameOf("Source"), overrides = listOf(Prop("name")))),
    )

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "internalSealedParent" to sealedCopy(internalSealedClassParent()),
        "visibilityOverridePublic" to sealedCopy(publicParent(), visibility = CopyVisibility.PUBLIC),
        "visibilityOverrideInternal" to sealedCopy(publicParent(), visibility = CopyVisibility.INTERNAL),
    )
