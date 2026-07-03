package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/** `excludes` on `@CombineMapping` — the annotation-level equivalent of `@Exclude` for external classes (issue #164). */
internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // `propA` (supplied by the primary source) loses its `= this.propA` default → required.
        "excludeMatchedProperty" to
            combineMapping(
                mappingHolder(),
                listOf(
                    dataClass("SourceA", Prop("propA"), Prop("valueA", INT)),
                    dataClass("SourceB", Prop("propB"), Prop("valueB", INT)),
                ),
                dataClass("Target", Prop("propA"), Prop("valueA", INT), Prop("propB"), Prop("valueB", INT)),
                excludes = listOf("propA"),
            ),
        // A name matching no auto-defaulted parameter is a no-op and emits a KSP warning.
        "excludeUnmatchedNameWarns" to
            combineMapping(
                mappingHolder(),
                listOf(
                    dataClass("SourceA", Prop("propA")),
                    dataClass("SourceB", Prop("propB")),
                ),
                dataClass("Target", Prop("propA"), Prop("propB")),
                excludes = listOf("nonexistent"),
            ),
    )
