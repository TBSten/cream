package me.tbsten.cream.ksp.feature.copyFrom.scenario

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
            copyFrom(
                dataClass("Target", Prop("publicProp"), Prop("extra")),
                dataClass(
                    "Source",
                    Prop("publicProp"),
                    Prop("internalProp", INT, visibility = INTERNAL),
                    Prop("privateProp", BOOLEAN, visibility = PRIVATE),
                ),
            ),
        "internalTargetClass" to
            copyFrom(
                clazz("Target", Prop("name"), Prop("extra", INT), modifiers = listOf(INTERNAL, DATA)),
                dataClass("Source", Prop("name")),
            ),
        "visibilityOverridePublic" to
            copyFrom(dataClass("Target", Prop("name")), dataClass("Source", Prop("name")), visibility = CopyVisibility.PUBLIC),
        "visibilityOverrideInternal" to
            copyFrom(dataClass("Target", Prop("name")), dataClass("Source", Prop("name")), visibility = CopyVisibility.INTERNAL),
    )
