package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun publicChildParent(): TypeSpec = sealedInterfaceParent("Source", children = listOf(childClass("Child", classNameOf("Source"), extras = listOf(Prop("name")))))

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "internalChildClass" to
            copyToChildren(
                sealedInterfaceParent("Source"),
                childClass("Child", classNameOf("Source"), extras = listOf(Prop("name")), modifiers = listOf(INTERNAL, DATA)),
            ),
        "visibilityOverridePublic" to copyToChildren(publicChildParent(), visibility = CopyVisibility.PUBLIC),
        "visibilityOverrideInternal" to copyToChildren(publicChildParent(), visibility = CopyVisibility.INTERNAL),
    )
