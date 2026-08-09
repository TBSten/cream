package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.INTERNAL
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "internalChildClass" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children = listOf(childClass("Open", classNameOf("Source"), props = listOf(Prop("name")))),
                ),
                childClass("Hidden", classNameOf("Source"), props = listOf(Prop("value")), modifiers = listOf(INTERNAL, DATA)),
            ),
        "visibilityOverrideInternal" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Child", classNameOf("Source"), props = listOf(Prop("name"))),
                            objectChild("Empty", classNameOf("Source")),
                        ),
                ),
                visibility = CopyVisibility.INTERNAL,
            ),
    )
