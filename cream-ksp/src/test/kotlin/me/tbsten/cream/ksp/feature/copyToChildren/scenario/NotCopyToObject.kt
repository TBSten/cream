package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun dataAndObjectParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        children =
            listOf(
                childClass("DataChild", classNameOf("Source"), extras = listOf(Prop("a"))),
                objectChild("ObjectChild", classNameOf("Source")),
            ),
    )

private fun allObjectsParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        children =
            listOf(
                objectChild("First", classNameOf("Source")),
                objectChild("Second", classNameOf("Source")),
            ),
    )

internal fun notCopyToObjectScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "objectChildKept" to copyToChildren(dataAndObjectParent()),
        "objectChildSuppressed" to copyToChildren(dataAndObjectParent(), notCopyToObject = true),
        "allObjectsSuppressedEmptyFile" to copyToChildren(allObjectsParent(), notCopyToObject = true),
    )
