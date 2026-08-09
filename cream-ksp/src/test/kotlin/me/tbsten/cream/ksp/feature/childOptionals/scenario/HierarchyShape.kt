package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun hierarchyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "leafPropertiesSweep" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(Prop("data"))),
                            childClass("Failure", classNameOf("Source"), props = listOf(Prop("error", INT))),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        "transitiveNestedLeaves" to
            childOptionals(
                sealedInterfaceParent(
                    "Root",
                    children =
                        listOf(
                            nestedSealed(
                                "Middle",
                                classNameOf("Root"),
                                childClass("Leaf", classNameOf("Root", "Middle"), props = listOf(Prop("data"))),
                            ),
                            objectChild("Other", classNameOf("Root")),
                        ),
                ),
            ),
        "mergedSameNameLeafProps" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(Prop("value"))),
                            childClass("Failure", classNameOf("Source"), props = listOf(Prop("value"))),
                        ),
                ),
            ),
    )
