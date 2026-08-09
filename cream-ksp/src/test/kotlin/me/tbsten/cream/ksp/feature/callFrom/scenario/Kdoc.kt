package me.tbsten.cream.ksp.feature.callFrom.scenario

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
            callFrom(
                fn("notify", param("message")),
                dataClass("NotifyArgs", Prop("message")),
                kdoc = kdocValue("Custom description for the bridge function."),
            ),
        "descriptionAndExamples" to
            callFrom(
                fn("notify", param("message")),
                dataClass("NotifyArgs", Prop("message")),
                kdoc = kdocValue("Use this only when migrating.", "# Recommended\n\nnotify(NotifyArgs(\"hi\"))"),
            ),
    )
