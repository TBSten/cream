package me.tbsten.cream.ksp.feature.combineFrom

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.combineFrom.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.matchingScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.multiSourceScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.repeatableScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.sourceKindScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.targetKindScenarios
import me.tbsten.cream.ksp.feature.combineFrom.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union
import me.tbsten.cream.ksp.testing.poet.toFileSpec

/**
 * Golden snapshot coverage for `@CombineFrom` (target-annotated N→1 combine, `@Repeatable`).
 * See `.claude/skills/cream-snapshot-test`.
 *
 * Most families combine from 2 sources (SourceA = receiver, SourceB = leading param) to show the real
 * N→1 combine shape; `multiSource/singleSource` pins the degenerate 1-source path (= copyFrom).
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - multi-target `funName` reject — `@CombineFrom` always emits ONE merged function, so no multi-target name
 *   collision exists; cross-occurrence funName agreement is covered by `repeatable/{stackedAnnotationsSameFunName,
 *   conflictingFunNamesRejected}`.
 * - nesting / multi-constructor target — redundant with the shared combine/copy core.
 * - `generics` is intentionally single-source (it isolates the type-param-MERGE axis on the receiver); the
 *   2-generic-source combine lives in `multiSource/multiSourceGenerics` (a 2nd source with an unconsumed type
 *   param leaks `T` into combineTo's per-source function, so 2-source generics is not uniform across combines).
 * - `targetKind` is single-source — its cases are object/reject target-dispatch where the sources don't combine.
 * - annotation-class TARGET — combineTo captures the representative (`targetKind/annotationClassTarget`):
 *   combine ACCEPTS + compiles it (Kotlin 1.6+), copy REJECTS it. The still-uncompilable #132 cases are
 *   abstract/inner/private-ctor.
 * - `typealias` source/target — `SnapshotScenario` can't carry a `TypeAliasSpec` → integration / EdgeUsage.
 * - #132 + #134 (stacked occurrences merge into one fn) are FROZEN as `// TODO(#132)` / `// TODO(#134)` goldens.
 */
internal class CombineFromSnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sourceKind" case sourceKindScenarios()
                        "targetKind" case targetKindScenarios()
                        "multiSource" case multiSourceScenarios()
                        "generics" case genericsScenarios()
                        "propertyShape" case propertyShapeScenarios()
                        "matching" case matchingScenarios()
                        "map" case mapScenarios()
                        "exclude" case excludeScenarios()
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
                        "funName" case funNameScenarios()
                        "repeatable" case repeatableScenarios()
                    }
                },
                Generator.validCreamOptions(),
                label = { scenarioLabel, optionsLabel -> "option=$optionsLabel/$scenarioLabel" },
            ).representativeValues()
                .forEach { (testCaseName, value) ->
                    val (scenario, creamOptions) = value

                    testCaseName!! {
                        runCompileSnapshotTest(input = scenario.toFileSpec(), options = creamOptions)
                    }
                }
        }
    })
