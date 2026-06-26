package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.CodeBlock
import me.tbsten.cream.KDoc
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
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

internal fun kdocScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "description" to
            copyFrom(
                dataClass("Target", Prop("name")),
                dataClass("Source", Prop("name")),
                kdoc = kdocValue("Custom description for the copy function."),
            ),
        "descriptionAndExamples" to
            copyFrom(
                dataClass("Target", Prop("name")),
                dataClass("Source", Prop("name")),
                kdoc = kdocValue("Use this only when migrating.", "# Recommended\n\nval target = source.copyToTarget()"),
            ),
    )
