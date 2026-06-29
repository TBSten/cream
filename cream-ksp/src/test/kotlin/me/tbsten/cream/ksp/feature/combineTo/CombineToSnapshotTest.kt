package me.tbsten.cream.ksp.feature.combineTo

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.combineTo.scenario.deprecatedScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.matchingScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.multiSourceScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.sourceKindScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.targetKindScenarios
import me.tbsten.cream.ksp.feature.combineTo.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union
import me.tbsten.cream.ksp.testing.poet.toFileSpecs

/**
 * Golden snapshot coverage for `@CombineTo` (source-annotated N→1 combine). See `.claude/skills/cream-snapshot-test`.
 *
 * Most families combine 2 sources (both annotated `@CombineTo(Target)`, SourceA = receiver, SourceB = leading
 * param) to show the real N→1 combine shape; `multiSource/singleSource` pins the degenerate 1-source path (= copyTo).
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - No `repeatable` family — `@CombineTo` is not `@Repeatable` (targets are one `vararg`; a duplicate target is the
 *   `targetKind/duplicateTargetRejected` reject, not a stack).
 * - `generics` is intentionally single-source (it isolates the type-param-MERGE axis on the receiver); the
 *   2-generic-source combine lives in `multiSource/multiSourceGenerics`. A 2nd source with an unconsumed type
 *   param leaks `T` into the per-source function (uncompilable), so 2-source generics is not uniform here.
 * - `targetKind` is single-source — its cases are object/reject target-dispatch where the sources don't combine.
 *   `targetKind/annotationClassTarget` captures the copy/combine divergence: combine ACCEPTS an annotation-class
 *   target and emits COMPILABLE code (Kotlin 1.6+ allows annotation instantiation), whereas copy REJECTS it. So
 *   #132's "uncompilable annotation target" premise no longer reproduces; the still-uncompilable #132 cases are
 *   abstract/inner/private-ctor (frozen with `// TODO(#132)`).
 * - multi-constructor target / nesting — the per-target `getConstructors().forEach` loop and FQ-name resolution are
 *   copyTo's shared paths → covered by copyTo's `constructor` / `nesting`.
 * - `typealias` source/target — `SnapshotScenario` can't carry a `TypeAliasSpec` → integration / EdgeUsage.
 * - #132 (combine lacks copy's `concreteClassRejection`) is FROZEN as `// TODO(#132)` goldens, not patched.
 */
internal class CombineToSnapshotTest :
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
                        "deprecated" case deprecatedScenarios()
                    }
                },
                Generator.validCreamOptions(),
                label = { scenarioLabel, optionsLabel -> "option=$optionsLabel/$scenarioLabel" },
            ).representativeValues()
                .forEach { (testCaseName, value) ->
                    val (scenario, creamOptions) = value

                    testCaseName!! {
                        runCompileSnapshotTest(inputs = scenario.toFileSpecs(), options = creamOptions)
                    }
                }
        }
    })
