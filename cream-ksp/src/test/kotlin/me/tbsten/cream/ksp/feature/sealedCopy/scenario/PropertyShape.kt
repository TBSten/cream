package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun matchedPropsScenario(vararg props: Prop): SnapshotScenario =
    sealedCopy(
        sealedInterfaceParent(
            "Source",
            abstractProps = props.toList(),
            children = listOf(childClass("Child", classNameOf("Source"), overrides = props.toList())),
        ),
    )

internal fun propertyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "stringProp" to matchedPropsScenario(Prop("name")),
        "mixedPrimitives" to matchedPropsScenario(Prop("name"), Prop("age", INT), Prop("active", BOOLEAN), Prop("score", DOUBLE)),
        "nullableProp" to matchedPropsScenario(Prop("name", STRING.copy(nullable = true))),
        "collectionProp" to matchedPropsScenario(Prop("tags", LIST.parameterizedBy(STRING))),
        "customTypeProp" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("data", classNameOf("Data"))),
                    children = listOf(childClass("Child", classNameOf("Source"), overrides = listOf(Prop("data", classNameOf("Data"))))),
                ),
                dataClass("Data", Prop("value")),
            ),
    )
