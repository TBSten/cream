package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun nestedBranch(
    name: String,
    parent: ClassName,
    vararg leaves: TypeSpec,
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .addSuperinterface(parent)
        .apply { leaves.forEach { addType(it) } }
        .build()

private fun enumLeaf(): TypeSpec =
    TypeSpec
        .enumBuilder("Child")
        .addSuperinterface(classNameOf("Source"))
        .addEnumConstant("A")
        .addEnumConstant("B")
        .build()

internal fun hierarchyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "dataClassChildren" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), extras = listOf(Prop("a"))),
                            childClass("Success", classNameOf("Source"), extras = listOf(Prop("a"), Prop("b", INT))),
                        ),
                ),
            ),
        "sharedPropsDirectChildren" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("value")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("value"))),
                            childClass("Success", classNameOf("Source"), overrides = listOf(Prop("value")), extras = listOf(Prop("count", INT))),
                        ),
                ),
            ),
        "transitiveSharedProps" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("id")),
                    children =
                        listOf(
                            nestedBranch(
                                "Branch",
                                classNameOf("Source"),
                                childClass("Done", classNameOf("Source", "Branch"), overrides = listOf(Prop("id")), extras = listOf(Prop("note"))),
                            ),
                        ),
                ),
            ),
        "objectChild" to copyToChildren(sealedInterfaceParent("Source", children = listOf(objectChild("Only", classNameOf("Source"))))),
        "nestedSealedChildren" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            nestedBranch(
                                "Branch",
                                classNameOf("Source"),
                                childClass("Done", classNameOf("Source", "Branch"), extras = listOf(Prop("x"))),
                                childClass("Pending", classNameOf("Source", "Branch"), extras = listOf(Prop("y", INT))),
                            ),
                        ),
                ),
            ),
        "mixedChildren" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("DataChild", classNameOf("Source"), extras = listOf(Prop("a"))),
                            objectChild("ObjectChild", classNameOf("Source")),
                            nestedBranch("Branch", classNameOf("Source"), childClass("Leaf", classNameOf("Source", "Branch"), extras = listOf(Prop("b", INT)))),
                        ),
                ),
            ),
        "enumChildRejected" to copyToChildren(sealedInterfaceParent("Source"), enumLeaf()),
        "noChildren" to copyToChildren(sealedInterfaceParent("Source")),
    )
