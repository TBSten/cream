package me.tbsten.cream.ksp.feature.sealedCopy

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.hierarchyShapeScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.nonCopyableStrategyScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.repeatableScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.sealedParentKindScenarios
import me.tbsten.cream.ksp.feature.sealedCopy.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@SealedCopy` (sealed self-copy, type-preserving). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `RETURN_NULL` with all-copyable leaves → no nullable widening (gated on a non-copyable leaf existing);
 *   byte-identical to the default ERROR all-copyable case.
 * - `RETURN_AS_IS` / `RETURN_NULL` on a non-data-class non-copyable leaf — same NonCopyable branch as the `object`
 *   leaf used in goldens → byte-identical.
 * - `data object` vs plain `object` leaf — same `ClassKind.OBJECT` → byte-identical.
 * - A `@SealedCopy.Via` delegate that fails validation (missing coverage / unmapped parameter / multiple `@Via`)
 *   — a clean positioned cream error, covered by `SealedCopyInvalidUsageTest`; the happy-path delegation
 *   (including the subset + `@SealedCopy.Map` rename case) is covered here in `05--map`.
 * - No `zeroProps` — an empty `data class` is illegal Kotlin; `hierarchyShape/noAbstractProperties` covers the
 *   empty-param self-copy instead.
 * - `<T : Any?>` unbounded-type-param rendering is SHARED with copy (verified vs copyTo) — not a sealedCopy quirk.
 * - All 3 option variants are byte-identical (default funName `copy` + `CopyTargetSimpleName` token are
 *   option-independent); the option axis is kept only for suite uniformity.
 * - `@SealedCopy` + `@CopyToChildren` on one type — cross-feature, belongs in `MultipleDiagnosticsTest`.
 */
internal class SealedCopySnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sealedParentKind" case sealedParentKindScenarios()
                        "hierarchyShape" case hierarchyShapeScenarios()
                        "generics" case genericsScenarios()
                        "propertyShape" case propertyShapeScenarios()
                        "nonCopyableStrategy" case nonCopyableStrategyScenarios()
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
                        runCompileSnapshotTest(inputs = scenario.files, options = creamOptions)
                    }
                }
        }
    })
