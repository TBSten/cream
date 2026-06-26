package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.CHAR_SEQUENCE
import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OUT
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
            .classBuilder("Filled")
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

private fun extraTypeParamHierarchy(): TypeSpec {
    val t = TypeVariableName("T")
    val m = TypeVariableName("M")
    val tagged =
        TypeSpec
            .classBuilder("Tagged")
            .addModifiers(DATA)
            .addTypeVariable(t)
            .addTypeVariable(m)
            .addSuperinterface(classNameOf("Source").parameterizedBy(t))
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter("item", t)
                    .addParameter("meta", m)
                    .build(),
            ).addProperty(
                PropertySpec
                    .builder("item", t)
                    .addModifiers(OVERRIDE)
                    .initializer("item")
                    .build(),
            ).addProperty(PropertySpec.builder("meta", m).initializer("meta").build())
            .build()
    return TypeSpec
        .interfaceBuilder("Source")
        .addModifiers(SEALED)
        .addTypeVariable(t)
        .addProperty(PropertySpec.builder("item", t).build())
        .addType(tagged)
        .build()
}

private fun concreteFixedHierarchy(): TypeSpec {
    val t = TypeVariableName("T")
    val genericChild =
        TypeSpec
            .classBuilder("Box")
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
    val concreteChild =
        TypeSpec
            .classBuilder("IntBox")
            .addModifiers(DATA)
            .addSuperinterface(classNameOf("Source").parameterizedBy(INT))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("item", INT).build())
            .addProperty(
                PropertySpec
                    .builder("item", INT)
                    .addModifiers(OVERRIDE)
                    .initializer("item")
                    .build(),
            ).build()
    return TypeSpec
        .interfaceBuilder("Source")
        .addModifiers(SEALED)
        .addTypeVariable(t)
        .addProperty(PropertySpec.builder("item", t).build())
        .addType(genericChild)
        .addType(concreteChild)
        .build()
}

private fun multiTypeParamHierarchy(): TypeSpec {
    val k = TypeVariableName("K")
    val v = TypeVariableName("V")
    val child =
        TypeSpec
            .classBuilder("Pair2")
            .addModifiers(DATA)
            .addTypeVariable(k)
            .addTypeVariable(v)
            .addSuperinterface(classNameOf("Source").parameterizedBy(k, v))
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter("key", k)
                    .addParameter("value", v)
                    .build(),
            ).addProperty(
                PropertySpec
                    .builder("key", k)
                    .addModifiers(OVERRIDE)
                    .initializer("key")
                    .build(),
            ).addProperty(
                PropertySpec
                    .builder("value", v)
                    .addModifiers(OVERRIDE)
                    .initializer("value")
                    .build(),
            ).build()
    return TypeSpec
        .interfaceBuilder("Source")
        .addModifiers(SEALED)
        .addTypeVariable(k)
        .addTypeVariable(v)
        .addProperty(PropertySpec.builder("key", k).build())
        .addProperty(PropertySpec.builder("value", v).build())
        .addType(child)
        .build()
}

internal fun genericsScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "sharedTypeParam" to sealedCopy(genericHierarchy(TypeVariableName("T"))),
        "boundedTypeParam" to sealedCopy(genericHierarchy(TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T"))))),
        "extraTypeParamStarProjected" to sealedCopy(extraTypeParamHierarchy()),
        "varianceOutTypeParam" to sealedCopy(genericHierarchy(TypeVariableName("T", variance = OUT))),
        "multipleBoundsWhere" to
            sealedCopy(genericHierarchy(TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T")), CHAR_SEQUENCE))),
        "concreteFixedTypeParam" to sealedCopy(concreteFixedHierarchy()),
        "multiTypeParamParent" to sealedCopy(multiTypeParamHierarchy()),
    )
