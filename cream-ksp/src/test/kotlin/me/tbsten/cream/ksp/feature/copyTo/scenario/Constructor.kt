package me.tbsten.cream.ksp.feature.copyTo.scenario

import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.VARARG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun varargTarget(
    varargName: String = "values",
    elementType: TypeName = INT,
    extra: ParameterSpec? = null,
): TypeSpec =
    TypeSpec
        .classBuilder("Target")
        .primaryConstructor(
            FunSpec
                .constructorBuilder()
                .addParameter("name", STRING)
                .addParameter(ParameterSpec.builder(varargName, elementType, VARARG).build())
                .apply { extra?.let { addParameter(it) } }
                .build(),
        ).addProperty(PropertySpec.builder("name", STRING).initializer("name").build())
        .build()

internal fun constructorScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "multipleConstructors" to
            copyTo(
                dataClass("Source", Prop("name")),
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
            ),
        "varargParameter" to copyTo(dataClass("Source", Prop("name")), varargTarget()),
        "varargMatchedPrimitiveArray" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("values", ClassName("kotlin", "IntArray"))),
                varargTarget(),
            ),
        "varargMatchedObjectArray" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("tags", ARRAY.parameterizedBy(STRING))),
                varargTarget("tags", STRING),
            ),
        "varargNullableArrayRequired" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("values", ClassName("kotlin", "IntArray").copy(nullable = true))),
                varargTarget(),
            ),
        "varargInMiddle" to
            copyTo(
                dataClass(
                    "Source",
                    Prop("name"),
                    Prop("values", ClassName("kotlin", "IntArray")),
                    Prop("extra", INT),
                ),
                varargTarget("values", INT, ParameterSpec.builder("extra", INT).build()),
            ),
    )
