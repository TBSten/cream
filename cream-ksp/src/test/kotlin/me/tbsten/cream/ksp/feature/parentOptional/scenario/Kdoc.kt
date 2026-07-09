package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.CodeBlock
import me.tbsten.cream.KDoc
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun kdocValue(
    description: String,
    vararg examples: String,
): CodeBlock =
    CodeBlock
        .builder()
        .add("%T(%L = %S", KDoc::class, KDoc::description.name, description)
        .apply {
            if (examples.isNotEmpty()) {
                add(", %L = [", KDoc::examples.name)
                examples.forEachIndexed { index, example -> add(if (index == 0) "%S" else ", %S", example) }
                add("]")
            }
        }.add(")")
        .build()

private fun singleChildScenario(kdoc: CodeBlock): SnapshotScenario =
    SnapshotScenario(
        sealedInterfaceParent(
            "Source",
            children =
                listOf(
                    childClass("Child", classNameOf("Source"), props = listOf(parentOptionalProp("data", kdoc = kdoc))),
                    objectChild("Empty", classNameOf("Source")),
                ),
        ),
    )

internal fun kdocScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "description" to singleChildScenario(kdocValue("Custom description for the accessor.")),
        "descriptionAndExamples" to
            singleChildScenario(
                kdocValue("Read the payload without a cast.", "# Recommended\n\nval payload = state.data"),
            ),
        "mergedKdocUsesFirstEntry" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(parentOptionalProp("message", kdoc = kdocValue("First contributor's doc."))),
                            ),
                            childClass(
                                "Failure",
                                classNameOf("Source"),
                                props = listOf(parentOptionalProp("message", kdoc = kdocValue("Second contributor's doc."))),
                            ),
                        ),
                ),
            ),
    )
