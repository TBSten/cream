package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.INT
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
        // The DefaultAccessorPropertyName token resolves to the annotated property's own name, so
        // a suffix derives a name per property instead of spelling each one out.
        "derivedFromDefaultToken" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props =
                                    listOf(
                                        parentOptionalProp(
                                            "data",
                                            propertyNameCode = defaultAccessorPropertyNamePlus("OrNull"),
                                        ),
                                        parentOptionalProp(
                                            "count",
                                            type = INT,
                                            propertyNameCode = defaultAccessorPropertyNamePlus("OrNull"),
                                        ),
                                    ),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
    )
