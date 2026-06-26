package me.tbsten.cream.ksp.feature.combineFrom.scenario

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
            combineFrom(
                dataClass("Target", Prop("value"), Prop("extra", INT)),
                clazz("SourceA", Prop("value"), modifiers = listOf(VALUE), annotations = listOf(JVM_INLINE)),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "plainClassSource" to
            combineFrom(
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                clazz("SourceA", Prop("name")),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "sealedInterfaceSource" to
            combineFrom(
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                sealedInterface("SourceA", "name"),
                dataClass("SourceB", Prop("extra", INT)),
            ),
    )
