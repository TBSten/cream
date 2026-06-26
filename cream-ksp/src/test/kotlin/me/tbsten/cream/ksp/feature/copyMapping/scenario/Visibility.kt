package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.KModifier.PRIVATE
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "internalTargetClass" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("name")),
                clazz("Target", Prop("name"), Prop("extra", INT), modifiers = listOf(INTERNAL, DATA)),
            ),
        "propertyVisibilities" to
            copyMapping(
                mappingHolder(),
                dataClass(
                    "Source",
                    Prop("publicProp"),
                    Prop("internalProp", INT, visibility = INTERNAL),
                    Prop("privateProp", BOOLEAN, visibility = PRIVATE),
                ),
                dataClass("Target", Prop("publicProp"), Prop("extra")),
            ),
    )
