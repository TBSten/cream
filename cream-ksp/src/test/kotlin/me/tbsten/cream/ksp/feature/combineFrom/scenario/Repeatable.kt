package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.BOOLEAN
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
    return Generator.snapshotScenarios(
        // TODO(#134): stacked @CombineFrom occurrences are currently MERGED into one function (sources
        // flattened + deduped). This golden freezes that behavior; #134 proposes one function per
        // occurrence instead. Capture-and-report — do not change the generator.
        "stackedAnnotations" to
            SnapshotScenario(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN))
                    .withCombineFrom(classNameOf("SourceA"))
                    .withCombineFrom(classNameOf("SourceB")),
                sourceA,
                sourceB,
            ),
        "duplicateSourceDeduped" to
            SnapshotScenario(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT), Prop("propertyC", BOOLEAN))
                    .withCombineFrom(classNameOf("SourceA"))
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
        "conflictingFunNamesRejected" to
            SnapshotScenario(
                dataClass("Target", Prop("propertyA"), Prop("propertyB", INT))
                    .withCombineFrom(classNameOf("SourceA"), funName = CodeBlock.of("%S", "toFoo"))
                    .withCombineFrom(classNameOf("SourceB"), funName = CodeBlock.of("%S", "toBar")),
                sourceA,
                sourceB,
            ),
    )
}
