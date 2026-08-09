package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.classWithNested
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun nestingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "nestedSourceClass" to
            functionScenario(
                fn("register", param("name")).withCallFrom(classNameOf("Outer", "Args")),
                classWithNested("Outer", dataClass("Args", Prop("name"))),
            ),
        "functionInNestedClass" to
            SnapshotScenario(
                classWithNested(
                    "Outer",
                    TypeSpec
                        .classBuilder("Inner")
                        .addFunction(fn("process", param("value")).withCallFrom(classNameOf("ProcessArgs")))
                        .build(),
                ),
                dataClass("ProcessArgs", Prop("value")),
            ),
    )
