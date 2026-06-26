package me.tbsten.cream.ksp.feature.copyTo.scenario

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
        "targetNestedInSource" to SnapshotScenario(source.withCopyTo(classNameOf("Source", "Target")).containing(target)),
        "sourceNestedInTarget" to SnapshotScenario(target.containing(source.withCopyTo(classNameOf("Target")))),
        "bothNestedInOuter" to SnapshotScenario(classWithNested("Outer", source.withCopyTo(classNameOf("Outer", "Target")), target)),
        "targetNestedTwoLevelsInSource" to
            SnapshotScenario(source.withCopyTo(classNameOf("Source", "Middle", "Target")).containing(classWithNested("Middle", target))),
        "targetNestedTwoLevelsInNestedSource" to
            SnapshotScenario(
                classWithNested(
                    "Parent",
                    source.withCopyTo(classNameOf("Parent", "Source", "Middle", "Target")).containing(classWithNested("Middle", target)),
                ),
            ),
    )
}
