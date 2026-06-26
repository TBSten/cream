package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun deprecated(message: String): AnnotationSpec = AnnotationSpec.builder(Deprecated::class).addMember("%S", message).build()

internal fun deprecatedScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "perSourcePrecedence" to
            combinedInto(
                dataClass("Target", Prop("shared"), Prop("legacy", INT), Prop("extra", INT)),
                dataClass("SourceA", Prop("shared"), Prop("legacy", INT, paramAnnotation = deprecated("a-prop"))),
                clazz("SourceB", Prop("extra", INT), modifiers = listOf(DATA), annotations = listOf(deprecated("b-class"))),
            ),
    )
