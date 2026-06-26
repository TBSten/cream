package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.asInner
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.classWithNested
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun sealedInterfaceTarget(): TypeSpec {
    fun leaf(
        name: String,
        superInterface: ClassName,
        vararg extra: Prop,
    ): TypeSpec =
        TypeSpec
            .classBuilder(name)
            .addModifiers(DATA)
            .addSuperinterface(superInterface)
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter("name", STRING)
                    .apply { extra.forEach { addParameter(it.name, it.type) } }
                    .build(),
            ).addProperty(
                PropertySpec
                    .builder("name", STRING)
                    .addModifiers(OVERRIDE)
                    .initializer("name")
                    .build(),
            ).apply { extra.forEach { addProperty(PropertySpec.builder(it.name, it.type).initializer(it.name).build()) } }
            .build()

    val second =
        TypeSpec
            .interfaceBuilder("Second")
            .addModifiers(SEALED)
            .addSuperinterface(classNameOf("Target", "First"))
            .addType(leaf("Done", classNameOf("Target", "First", "Second")))
            .addType(leaf("Refreshing", classNameOf("Target", "First", "Second"), Prop("extra", INT)))
            .build()
    val first =
        TypeSpec
            .interfaceBuilder("First")
            .addModifiers(SEALED)
            .addSuperinterface(classNameOf("Target"))
            .addType(second)
            .build()
    return TypeSpec
        .interfaceBuilder("Target")
        .addModifiers(SEALED)
        .addProperty(PropertySpec.builder("name", STRING).build())
        .addType(first)
        .build()
}

internal fun targetKindScenarios(): Generator<SnapshotScenario> {
    val source = dataClass("Source", Prop("name"))
    return Generator.snapshotScenarios(
        "objectTarget" to copyFrom(TypeSpec.objectBuilder("Target").build(), source),
        "sealedInterfaceTarget" to copyFrom(sealedInterfaceTarget(), source),
        "abstractTarget" to copyFrom(clazz("Target", Prop("name"), Prop("extra", INT), modifiers = listOf(ABSTRACT)), source),
        "nonSealedInterfaceTarget" to
            copyFrom(TypeSpec.interfaceBuilder("Target").addProperty(PropertySpec.builder("name", STRING).build()).build(), source),
        "enumTarget" to
            copyFrom(
                TypeSpec
                    .enumBuilder("Target")
                    .addEnumConstant("A")
                    .addEnumConstant("B")
                    .build(),
                source,
            ),
        "innerTarget" to
            SnapshotScenario(
                classWithNested("Outer", clazz("Target", Prop("name")).withCopyFrom(classNameOf("Source")).asInner()),
                source,
            ),
        "privateConstructorTarget" to copyFrom(clazz("Target", Prop("name"), Prop("extra", INT), constructorVisibility = PRIVATE), source),
    )
}
