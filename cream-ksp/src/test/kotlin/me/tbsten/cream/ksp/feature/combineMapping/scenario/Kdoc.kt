package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
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

private val sources = listOf(dataClass("SourceA", Prop("name")), dataClass("SourceB", Prop("extra", INT)))
private val target = dataClass("Target", Prop("name"), Prop("extra", INT))

internal fun kdocScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "description" to combineMapping(mappingHolder(), sources, target, kdoc = kdocValue("Custom description for the combine function.")),
        "descriptionAndExamples" to
            combineMapping(
                mappingHolder(),
                sources,
                target,
                kdoc = kdocValue("Use this only when migrating.", "# Recommended\n\nval target = sourceA.copyToTarget(sourceB)"),
            ),
    )
