package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.classWithNested
import me.tbsten.cream.ksp.testing.poet.containing
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun nestingScenarios(): Generator<SnapshotScenario> {
    val source = dataClass("Source", Prop("name"))
    val target = dataClass("Target", Prop("name"), Prop("extra", INT))
    return Generator.snapshotScenarios(
        "sourceNestedInTarget" to SnapshotScenario(target.withCopyFrom(classNameOf("Target", "Source")).containing(source)),
        "targetNestedInSource" to SnapshotScenario(source.containing(target.withCopyFrom(classNameOf("Source")))),
        "bothNestedInOuter" to SnapshotScenario(classWithNested("Outer", source, target.withCopyFrom(classNameOf("Outer", "Source")))),
        "sourceNestedTwoLevelsInTarget" to
            SnapshotScenario(target.withCopyFrom(classNameOf("Target", "Middle", "Source")).containing(classWithNested("Middle", source))),
        "sourceNestedTwoLevelsInNestedTarget" to
            SnapshotScenario(
                classWithNested(
                    "Parent",
                    target.withCopyFrom(classNameOf("Parent", "Target", "Middle", "Source")).containing(classWithNested("Middle", source)),
                ),
            ),
    )
}
