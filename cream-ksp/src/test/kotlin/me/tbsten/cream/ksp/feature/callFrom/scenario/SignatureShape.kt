package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.SUSPEND
import com.squareup.kotlinpoet.KModifier.VARARG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun signatureShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "suspendFunction" to
            callFrom(
                FunSpec
                    .builder("load")
                    .addModifiers(SUSPEND)
                    .addParameter("id", STRING)
                    .returns(INT)
                    .addStatement("return id.length")
                    .build(),
                dataClass("LoadArgs", Prop("id")),
            ),
        // Non-array `items: String` property: a vararg parameter never matches a non-array
        // property, so `items` stays a required vararg.
        "varargParameter" to
            callFrom(
                FunSpec
                    .builder("log")
                    .addParameter(ParameterSpec.builder("items", STRING).addModifiers(VARARG).build())
                    .build(),
                dataClass("LogArgs", Prop("items")),
            ),
        // `items: Array<String>` property: the vararg element type is lifted to the corresponding
        // array type, so the parameter matches and gets a `= logArgs.items` default.
        "varargMatchesArrayProperty" to
            callFrom(
                FunSpec
                    .builder("log")
                    .addParameter(ParameterSpec.builder("items", STRING).addModifiers(VARARG).build())
                    .build(),
                dataClass("LogArgs", Prop("items", ARRAY.parameterizedBy(STRING))),
            ),
        // An unmatched parameter WITH a default on the original function is omitted from the
        // bridge (and the delegating call), so the original default still applies. KSP cannot
        // read the default expression, and transcribing the parameter without one would have
        // made an optional parameter required.
        "originalDefaultFallsBack" to
            callFrom(
                FunSpec
                    .builder("greet")
                    .addParameter("name", STRING)
                    .addParameter(ParameterSpec.builder("punctuation", STRING).defaultValue("%S", "!").build())
                    .build(),
                dataClass("GreetArgs", Prop("name")),
            ),
        // A MATCHED parameter keeps its place even when the original declares a default: the
        // auto-copied `= <source>.<prop>` default takes precedence over the original's.
        "originalDefaultOverriddenByMatch" to
            callFrom(
                FunSpec
                    .builder("greet")
                    .addParameter("name", STRING)
                    .addParameter(ParameterSpec.builder("suffix", STRING).defaultValue("%S", "!").build())
                    .build(),
                dataClass("GreetArgs", Prop("name"), Prop("suffix")),
            ),
    )
