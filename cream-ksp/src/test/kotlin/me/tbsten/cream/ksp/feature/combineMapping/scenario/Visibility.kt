package me.tbsten.cream.ksp.feature.combineMapping.scenario

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

private val partnerSource = dataClass("SourceB", Prop("extra", INT))

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "internalTargetClass" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("name")), partnerSource),
                clazz("Target", Prop("name"), Prop("extra", INT), modifiers = listOf(INTERNAL, DATA)),
            ),
        "propertyVisibilities" to
            combineMapping(
                mappingHolder(),
                listOf(
                    dataClass(
                        "SourceA",
                        Prop("publicProp"),
                        Prop("internalProp", INT, visibility = INTERNAL),
                        Prop("privateProp", BOOLEAN, visibility = PRIVATE),
                    ),
                    partnerSource,
                ),
                dataClass("Target", Prop("publicProp"), Prop("extra", INT)),
            ),
        "visibilityOverridePublic" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("name")), partnerSource),
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                visibility = CopyVisibility.PUBLIC,
            ),
        "visibilityOverrideInternal" to
            combineMapping(
                mappingHolder(),
                listOf(dataClass("SourceA", Prop("name")), partnerSource),
                dataClass("Target", Prop("name"), Prop("extra", INT)),
                visibility = CopyVisibility.INTERNAL,
            ),
    )
