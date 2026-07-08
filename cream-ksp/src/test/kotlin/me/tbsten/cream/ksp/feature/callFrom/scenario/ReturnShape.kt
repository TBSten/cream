package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.NOTHING
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * Return-type transcription beyond the plain / Unit / generic returns the other families already
 * cover: `Nothing` and nullable returns.
 */
internal fun returnShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "nothingReturn" to
            callFrom(
                FunSpec
                    .builder("fail")
                    .addParameter("message", STRING)
                    .returns(NOTHING)
                    .addStatement("throw IllegalStateException(message)")
                    .build(),
                dataClass("FailArgs", Prop("message")),
            ),
        "nullableReturn" to
            callFrom(
                FunSpec
                    .builder("find")
                    .addParameter("id", STRING)
                    .returns(STRING.copy(nullable = true))
                    .addStatement("return null")
                    .build(),
                dataClass("FindArgs", Prop("id")),
            ),
    )
