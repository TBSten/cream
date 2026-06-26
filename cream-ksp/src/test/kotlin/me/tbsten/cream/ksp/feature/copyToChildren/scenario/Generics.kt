package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun genericHierarchy(t: TypeVariableName): TypeSpec {
    val child =
        TypeSpec
            .classBuilder("Child")
            .addModifiers(DATA)
            .addTypeVariable(t)
            .addSuperinterface(classNameOf("Source").parameterizedBy(t))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("item", t).build())
            .addProperty(
                PropertySpec
                    .builder("item", t)
                    .addModifiers(OVERRIDE)
                    .initializer("item")
                    .build(),
            ).build()
    return TypeSpec
        .interfaceBuilder("Source")
        .addModifiers(SEALED)
        .addTypeVariable(t)
        .addProperty(PropertySpec.builder("item", t).build())
        .addType(child)
        .build()
}

internal fun genericsScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "sharedTypeParam" to copyToChildren(genericHierarchy(TypeVariableName("T"))),
        "boundedTypeParam" to copyToChildren(genericHierarchy(TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T"))))),
    )
