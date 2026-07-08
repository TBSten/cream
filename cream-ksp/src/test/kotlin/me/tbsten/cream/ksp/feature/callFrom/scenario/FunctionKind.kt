package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun functionKindScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "topLevelFunction" to
            callFrom(
                FunSpec
                    .builder("processData")
                    .addParameter("data1", STRING)
                    .addParameter("data2", INT)
                    .returns(STRING)
                    .addStatement("return data1")
                    .build(),
                dataClass("ProcessDataArgs", Prop("data1"), Prop("data2", INT)),
            ),
        "memberFunction" to
            SnapshotScenario(
                TypeSpec
                    .classBuilder("DataProcessor")
                    .addFunction(fn("process", param("value")).withCallFrom(classNameOf("ProcessArgs")))
                    .build(),
                dataClass("ProcessArgs", Prop("value")),
            ),
        "objectMemberFunction" to
            SnapshotScenario(
                TypeSpec
                    .objectBuilder("Handler")
                    .addFunction(fn("handle", param("value")).withCallFrom(classNameOf("HandleArgs")))
                    .build(),
                dataClass("HandleArgs", Prop("value")),
            ),
        // A companion-object member bridges as `fun Enclosing.Companion.make(...)`.
        "companionObjectMemberFunction" to
            SnapshotScenario(
                TypeSpec
                    .classBuilder("Factory")
                    .addType(
                        TypeSpec
                            .companionObjectBuilder()
                            .addFunction(fn("make", param("value")).withCallFrom(classNameOf("MakeArgs")))
                            .build(),
                    ).build(),
                dataClass("MakeArgs", Prop("value")),
            ),
    )
