package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.VALUE
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.sealedInterface
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val JVM_INLINE = AnnotationSpec.builder(ClassName("kotlin.jvm", "JvmInline")).build()

internal fun sourceKindScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "valueClassSource" to
            copyFrom(
                dataClass("Target", Prop("value")),
                clazz("Source", Prop("value"), modifiers = listOf(VALUE), annotations = listOf(JVM_INLINE)),
            ),
        "plainClassSource" to copyFrom(dataClass("Target", Prop("name"), Prop("extra", INT)), clazz("Source", Prop("name"))),
        "sealedInterfaceSource" to copyFrom(dataClass("Target", Prop("name"), Prop("extra", INT)), sealedInterface("Source", "name")),
    )
