package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(SealedCopy.Exclude::class).build()

private fun noEffectParent(): TypeSpec =
    TypeSpec
        .interfaceBuilder("Source")
        .addModifiers(SEALED)
        .addProperty(
            PropertySpec
                .builder("tag", STRING)
                .addAnnotation(EXCLUDE)
                .getter(FunSpec.getterBuilder().addStatement("return %S", "tag").build())
                .build(),
        ).addType(childClass("Child", classNameOf("Source"), extras = listOf(Prop("name"))))
        .build()

internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "excludedAbstractProperty" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name"), Prop("count", INT, paramAnnotation = EXCLUDE)),
                    children = listOf(childClass("Child", classNameOf("Source"), overrides = listOf(Prop("name"), Prop("count", INT)))),
                ),
            ),
        "excludeNoEffect" to sealedCopy(noEffectParent()),
    )
