package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.KDoc
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
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

private fun singleChildParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        children =
            listOf(
                childClass("Child", classNameOf("Source"), props = listOf(Prop("name"))),
                objectChild("Empty", classNameOf("Source")),
            ),
    )

internal fun kdocScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "description" to childOptionals(singleChildParent(), kdoc = kdocValue("Custom description for the swept accessors.")),
        "descriptionAndExamples" to
            childOptionals(
                singleChildParent(),
                kdoc = kdocValue("Read the leaf value without a cast.", "# Recommended\n\nval name = state.name"),
            ),
    )
