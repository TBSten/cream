package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.INTERNAL
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun childScenario(
    childModifiers: List<KModifier> = listOf(DATA),
    propVisibility: KModifier? = null,
    annotationVisibility: CopyVisibility? = null,
): SnapshotScenario {
    val prop =
        Prop(
            "data",
            visibility = propVisibility,
            paramAnnotation = parentOptional(visibility = annotationVisibility),
        )
    return SnapshotScenario(
        sealedInterfaceParent("Source", children = listOf(objectChild("Empty", classNameOf("Source")))),
        childClass("Child", classNameOf("Source"), props = listOf(prop), modifiers = childModifiers),
    )
}

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "internalChildClass" to childScenario(childModifiers = listOf(INTERNAL, DATA)),
        "internalProperty" to childScenario(propVisibility = INTERNAL),
        "visibilityOverrideInternal" to childScenario(annotationVisibility = CopyVisibility.INTERNAL),
        "visibilityOverridePublicOnInternalChild" to
            childScenario(childModifiers = listOf(INTERNAL, DATA), annotationVisibility = CopyVisibility.PUBLIC),
        // A public accessor may *read* an internal property (the getter body is not part of the
        // signature); only signature exposure (internal receiver / property type) is rejected —
        // see ParentOptionalInvalidUsageTest.forcedPublic*.
        "visibilityOverridePublicOnInternalProperty" to
            childScenario(propVisibility = INTERNAL, annotationVisibility = CopyVisibility.PUBLIC),
        "mergedNarrowestAnnotationWins" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(parentOptionalProp("message", visibility = CopyVisibility.INTERNAL)),
                            ),
                            childClass("Failure", classNameOf("Source"), props = listOf(parentOptionalProp("message"))),
                        ),
                ),
            ),
    )
