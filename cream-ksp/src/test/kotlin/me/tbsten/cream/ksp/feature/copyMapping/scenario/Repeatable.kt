package me.tbsten.cream.ksp.feature.copyMapping.scenario

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
    return Generator.snapshotScenarios(
        "multipleAnnotations" to
            SnapshotScenario(
                mappingHolder()
                    .withCopyMapping(classNameOf("LibA"), classNameOf("LibB"))
                    .withCopyMapping(classNameOf("LibB"), classNameOf("LibC")),
                libA,
                libB,
                libC,
            ),
    )
}
