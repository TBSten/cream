package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun repeatableScenarios(): Generator<SnapshotScenario> {
    val libA = dataClass("LibA", Prop("shared"), Prop("aProp", INT))
    val libB = dataClass("LibB", Prop("shared"), Prop("bProp", INT))
    val libC = dataClass("LibC", Prop("shared"), Prop("cProp", INT))
    val target = dataClass("Target", Prop("shared"), Prop("aProp", INT), Prop("bProp", INT))
    return Generator.snapshotScenarios(
        "multipleAnnotations" to
            SnapshotScenario(
                listOf(
                    mappingHolder()
                        .withCombineMapping(listOf(classNameOf("LibA"), classNameOf("LibB")), classNameOf("Target"))
                        .withCombineMapping(listOf(classNameOf("LibB"), classNameOf("LibC")), classNameOf("Target")),
                    libA,
                    libB,
                    libC,
                    target,
                ),
            ),
    )
}
