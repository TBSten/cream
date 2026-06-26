package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.KModifier.PRIVATE
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "propertyVisibilities" to
            combinedInto(
                dataClass("Target", Prop("publicProp"), Prop("extra", INT)),
                dataClass(
                    "SourceA",
                    Prop("publicProp"),
                    Prop("internalProp", INT, visibility = INTERNAL),
                    Prop("privateProp", BOOLEAN, visibility = PRIVATE),
                ),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "internalTargetClass" to
            combinedInto(
                clazz("Target", Prop("name"), Prop("extra", INT), modifiers = listOf(INTERNAL, DATA)),
                dataClass("SourceA", Prop("name")),
                dataClass("SourceB", Prop("extra", INT)),
            ),
        "visibilityOverridePublic" to
            combinedInto(
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                dataClass("SourceA", Prop("name")),
                dataClass("SourceB", Prop("extra", INT)),
                visibility = CopyVisibility.PUBLIC,
            ),
        "visibilityOverrideInternal" to
            combinedInto(
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                dataClass("SourceA", Prop("name")),
                dataClass("SourceB", Prop("extra", INT)),
                visibility = CopyVisibility.INTERNAL,
            ),
    )
