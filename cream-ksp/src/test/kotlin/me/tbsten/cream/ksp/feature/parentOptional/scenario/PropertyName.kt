package me.tbsten.cream.ksp.feature.parentOptional.scenario

import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun propertyNameScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "renamedAccessor" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(parentOptionalProp("data", propertyName = "dataOrNull"))),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        "renameAvoidsMerge" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(parentOptionalProp("message"))),
                            childClass(
                                "Failure",
                                classNameOf("Source"),
                                props = listOf(parentOptionalProp("message", propertyName = "failureMessage")),
                            ),
                        ),
                ),
            ),
    )
