package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun sealedClassParent(): TypeSpec {
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
        .addModifiers(SEALED)
        .addProperty(PropertySpec.builder("name", STRING).addModifiers(ABSTRACT).build())
        .addType(child)
        .build()
}

internal fun sealedParentKindScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "sealedInterfaceParent" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name")),
                    children = listOf(childClass("Child", classNameOf("Source"), overrides = listOf(Prop("name")))),
                ),
            ),
        "sealedClassParent" to sealedCopy(sealedClassParent()),
        "nonSealedParentRejected" to sealedCopy(dataClass("Source", Prop("name"))),
    )
