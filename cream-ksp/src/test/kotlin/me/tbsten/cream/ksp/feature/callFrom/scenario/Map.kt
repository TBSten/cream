package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.CallFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun mapAnno(vararg sourcePropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CallFrom.Map::class).apply { sourcePropertyNames.forEach { addMember("%S", it) } }.build()

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "parameterMapping" to
            callFrom(
                fn("send", param("paramName", STRING, mapAnno("sourceName")), param("shared")),
                dataClass("SendArgs", Prop("sourceName"), Prop("shared")),
            ),
        "mapToNonexistentProperty" to
            callFrom(
                fn("send", param("paramName", STRING, mapAnno("missingSource")), param("shared")),
                dataClass("SendArgs", Prop("sourceName"), Prop("shared")),
            ),
        "mapOverridesNameMatch" to
            callFrom(
                fn("send", param("alpha", STRING, mapAnno("beta"))),
                dataClass("SendArgs", Prop("alpha"), Prop("beta")),
            ),
    )
