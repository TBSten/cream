package me.tbsten.cream.ksp.feature.copyTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun deprecated(
    message: String,
    level: DeprecationLevel? = null,
): AnnotationSpec =
    AnnotationSpec
        .builder(Deprecated::class)
        .addMember("%S", message)
        .apply { if (level != null) addMember("level = %T.%L", DeprecationLevel::class, level.name) }
        .build()

internal fun deprecatedScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "deprecatedSourceClass" to
            copyTo(
                clazz("Source", Prop("name"), modifiers = listOf(DATA), annotations = listOf(deprecated("use Target instead"))),
                dataClass("Target", Prop("name")),
            ),
        "deprecationLevelError" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("legacy", INT, paramAnnotation = deprecated("removed", level = DeprecationLevel.ERROR))),
                dataClass("Target", Prop("name")),
            ),
        "deprecatedSourceProperty" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("legacy", INT, paramAnnotation = deprecated("gone"))),
                dataClass("Target", Prop("name"), Prop("legacy", INT)),
            ),
    )
