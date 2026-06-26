package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.classWithNested
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun nestingScenarios(): Generator<SnapshotScenario> {
    val source = dataClass("Source", Prop("name"))
    val target = dataClass("Target", Prop("name"), Prop("extra", INT))
    return Generator.snapshotScenarios(
        "sourceNestedInOuter" to
            SnapshotScenario(
                mappingHolder().withCopyMapping(classNameOf("Outer", "Source"), classNameOf("Target")),
                classWithNested("Outer", source),
                target,
            ),
        "targetNestedInOuter" to
            SnapshotScenario(
                mappingHolder().withCopyMapping(classNameOf("Source"), classNameOf("Outer", "Target")),
                source,
                classWithNested("Outer", target),
            ),
        "bothNestedInSameOuter" to
            SnapshotScenario(
                mappingHolder().withCopyMapping(classNameOf("Outer", "Source"), classNameOf("Outer", "Target")),
                classWithNested("Outer", source, target),
            ),
    )
}
