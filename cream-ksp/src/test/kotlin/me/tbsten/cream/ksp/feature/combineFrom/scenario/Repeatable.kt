package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun repeatableScenarios(): Generator<SnapshotScenario> {
    val sourceA = dataClass("SourceA", Prop("propertyA"))
    val sourceB = dataClass("SourceB", Prop("propertyB", INT))
    val sourceC = dataClass("SourceC", Prop("propertyC"))
    val sourceD = dataClass("SourceD", Prop("propertyD", INT))
    return Generator.snapshotScenarios(
        "stackedAnnotations" to
            SnapshotScenario(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT))
                    .withCombineFrom(classNameOf("SourceA"))
                    .withCombineFrom(classNameOf("SourceB")),
                sourceA,
                sourceB,
            ),
        "stackedAnnotationsSameFunName" to
            SnapshotScenario(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT))
                    .withCombineFrom(classNameOf("SourceA"), funName = CodeBlock.of("%S", "toTarget"))
                    .withCombineFrom(classNameOf("SourceB"), funName = CodeBlock.of("%S", "toTarget")),
                sourceA,
                sourceB,
            ),
        // Each stacked occurrence combines a DISTINCT pair of sources under its own funName, so the
        // two generated functions are independent combines (not just single-source overloads).
        "stackedAnnotationsDistinctFunNames" to
            SnapshotScenario(
                dataClass(
                    "Target",
                    Prop("propertyA"),
                    Prop("propertyB", INT),
                    Prop("propertyC"),
                    Prop("propertyD", INT),
                ).withCombineFrom(classNameOf("SourceA"), classNameOf("SourceB"), funName = CodeBlock.of("%S", "toFoo"))
                    .withCombineFrom(classNameOf("SourceC"), classNameOf("SourceD"), funName = CodeBlock.of("%S", "toBar")),
                sourceA,
                sourceB,
                sourceC,
                sourceD,
            ),
        "duplicateOccurrenceRejected" to
            SnapshotScenario(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT))
                    .withCombineFrom(classNameOf("SourceA"))
                    .withCombineFrom(classNameOf("SourceA")),
                sourceA,
            ),
        "duplicateSourceWithinOccurrence" to
            SnapshotScenario(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT))
                    .withCombineFrom(classNameOf("SourceA"), classNameOf("SourceA")),
                sourceA,
            ),
    )
}
