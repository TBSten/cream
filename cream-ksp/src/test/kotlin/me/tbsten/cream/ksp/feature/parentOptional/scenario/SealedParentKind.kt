package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun sealedClassParent(): TypeSpec {
    val child =
        TypeSpec
            .classBuilder("Child")
            .addModifiers(DATA)
            .superclass(classNameOf("Source"))
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter(ParameterSpec.builder("value", INT).addAnnotation(parentOptional()).build())
                    .build(),
            ).addProperty(PropertySpec.builder("value", INT).initializer("value").build())
            .build()
    return TypeSpec
        .classBuilder("Source")
        .addModifiers(SEALED)
        .addType(child)
        .build()
}

internal fun sealedParentKindScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "sealedInterfaceParent" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(parentOptionalProp("data"))),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        "sealedClassParent" to SnapshotScenario(sealedClassParent()),
    )
