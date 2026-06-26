package me.tbsten.cream.ksp.feature.copyTo.scenario

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
            copyTo(
                dataClass(
                    "Source",
                    Prop("publicProp"),
                    Prop("internalProp", INT, visibility = INTERNAL),
                    Prop("privateProp", BOOLEAN, visibility = PRIVATE),
                ),
                dataClass("Target", Prop("publicProp"), Prop("extra")),
            ),
        "internalSourceClass" to
            copyTo(
                clazz("Source", Prop("name"), modifiers = listOf(INTERNAL, DATA)),
                dataClass("Target", Prop("name"), Prop("extra", INT)),
            ),
        "visibilityOverridePublic" to
            copyTo(dataClass("Source", Prop("name")), dataClass("Target", Prop("name")), visibility = CopyVisibility.PUBLIC),
        "visibilityOverrideInternal" to
            copyTo(dataClass("Source", Prop("name")), dataClass("Target", Prop("name")), visibility = CopyVisibility.INTERNAL),
    )
