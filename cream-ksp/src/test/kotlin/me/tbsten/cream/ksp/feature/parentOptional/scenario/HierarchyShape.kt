package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun hierarchyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "siblingsWithoutAnnotation" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(parentOptionalProp("data"))),
                            childClass("Failure", classNameOf("Source"), props = listOf(Prop("error", INT))),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        "intermediateSealedAncestors" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Root",
                    children =
                        listOf(
                            nestedSealed(
                                "Middle",
                                classNameOf("Root"),
                                childClass("Leaf", classNameOf("Root", "Middle"), props = listOf(parentOptionalProp("data"))),
                            ),
                            objectChild("Other", classNameOf("Root")),
                        ),
                ),
            ),
        "multipleAccessorsOnOneParent" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("First", classNameOf("Source"), props = listOf(parentOptionalProp("id"))),
                            childClass("Second", classNameOf("Source"), props = listOf(parentOptionalProp("count", INT))),
                        ),
                ),
            ),
    )
