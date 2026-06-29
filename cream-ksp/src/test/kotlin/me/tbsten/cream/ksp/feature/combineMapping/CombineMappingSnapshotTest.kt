package me.tbsten.cream.ksp.feature.combineMapping

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.combineMapping.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.matchingScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.multiSourceScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.repeatableScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.sourceKindScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.sourceKindValidationScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.targetKindScenarios
import me.tbsten.cream.ksp.feature.combineMapping.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@CombineMapping` (holder-annotated library N→1 combine). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `@Exclude` — `@CombineMapping` has none; `canReverse` — N→1 is not invertible. Both N/A.
 * - sealed / interface / object SOURCES — rejected by source-kind validation (`sourceKindValidation/nonClassSource`),
 *   so they are reject cases, not positive `sourceKind` cases.
 * - annotation-class source ACCEPTED — allowed but degenerate; the scenario builders don't emit one → EdgeUsage.
 * - No `zeroProps` — an empty `data class` is illegal Kotlin (and ≥2 sources are required anyway).
 * - `funName` is literal+token only (no multi-target reject) — exactly one function per annotation, so a plain
 *   literal is always legal.
 * - nesting / constructor families — redundant with combineTo's shared core (FQ resolution / per-ctor loop).
 * - `typealias` sources/target — `SnapshotScenario` can't carry a `TypeAliasSpec` → integration `TypeAliasTest`.
 * - cross-package `@Repeatable` multi-file grouping (`groupBy { sourceClasses.first().packageName }`) + the
 *   one-bad-annotation-suppresses-the-holder short-circuit — a single `GENERATED_PACKAGE` can't express
 *   multi-package input → EdgeUsage / InvalidUsage.
 * - #132 (combine target validation) is FROZEN as `// TODO(#132)` goldens, not patched.
 */
internal class CombineMappingSnapshotTest :
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
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
                        "funName" case funNameScenarios()
                        "repeatable" case repeatableScenarios()
                        "sourceKindValidation" case sourceKindValidationScenarios()
                    }
                },
                Generator.validCreamOptions(),
                label = { scenarioLabel, optionsLabel -> "option=$optionsLabel/$scenarioLabel" },
            ).representativeValues()
                .forEach { (testCaseName, value) ->
                    val (scenario, creamOptions) = value

                    testCaseName!! {
                        runCompileSnapshotTest(inputs = scenario.files, options = creamOptions)
                    }
                }
        }
    })
