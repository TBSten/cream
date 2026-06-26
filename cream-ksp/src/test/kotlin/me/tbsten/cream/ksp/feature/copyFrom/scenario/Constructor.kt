package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.VARARG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun varargTarget(): TypeSpec =
    TypeSpec
        .classBuilder("Target")
        .primaryConstructor(
            FunSpec
                .constructorBuilder()
                .addParameter("name", STRING)
                .addParameter(ParameterSpec.builder("values", INT, VARARG).build())
                .build(),
        ).addProperty(PropertySpec.builder("name", STRING).initializer("name").build())
        .build()

internal fun constructorScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "multipleConstructors" to
            copyFrom(
                dataClass(
                    "Target",
                    Prop("name"),
                    Prop("extra", INT),
                    secondaryConstructors =
                        listOf(
                            FunSpec
                                .constructorBuilder()
                                .addParameter("name", STRING)
                                .callThisConstructor("name", "0")
                                .build(),
                            FunSpec.constructorBuilder().callThisConstructor("\"\"", "0").build(),
                        ),
                ),
                dataClass("Source", Prop("name")),
            ),
        "varargParameter" to copyFrom(varargTarget(), dataClass("Source", Prop("name"))),
        "varargMatchedPrimitiveArray" to
            copyFrom(
                varargTarget(),
                dataClass("Source", Prop("name"), Prop("values", ClassName("kotlin", "IntArray"))),
            ),
    )
