package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
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
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("value", INT).build())
            .addProperty(PropertySpec.builder("value", INT).initializer("value").build())
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
            copyToChildren(
                sealedInterfaceParent("Source", children = listOf(childClass("Child", classNameOf("Source"), extras = listOf(Prop("value", INT))))),
            ),
        "sealedClassParent" to copyToChildren(sealedClassParent()),
        "nonSealedParentRejected" to copyToChildren(dataClass("Source", Prop("name"))),
    )
