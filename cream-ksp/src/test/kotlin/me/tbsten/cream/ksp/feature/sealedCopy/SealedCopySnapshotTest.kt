package me.tbsten.cream.ksp.feature.sealedCopy

import io.kotest.core.spec.style.FunSpec
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
import me.tbsten.cream.ksp.testing.poet.toFileSpec

/**
 * Golden snapshot coverage for `@SealedCopy` (sealed self-copy, type-preserving). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `RETURN_NULL` with all-copyable leaves → no nullable widening (gated on a non-copyable leaf existing);
 *   byte-identical to the default ERROR all-copyable case.
 * - `RETURN_AS_IS` / `RETURN_NULL` on a non-data-class non-copyable leaf — same NonCopyable branch as the `object`
 *   leaf used in goldens → byte-identical.
 * - `data object` vs plain `object` leaf — same `ClassKind.OBJECT` → byte-identical.
 * - `@SealedCopy.Map` pointing at an incompatible delegate — produces uncompilable USER code (user error); the
 *   happy-path delegation is covered.
 * - No `zeroProps` — an empty `data class` is illegal Kotlin; `hierarchyShape/noAbstractProperties` covers the
 *   empty-param self-copy instead.
 * - `<T : Any?>` unbounded-type-param rendering is SHARED with copy (verified vs copyTo) — not a sealedCopy quirk.
 * - All 3 option variants are byte-identical (default funName `copy` + `CopyTargetSimpleName` token are
 *   option-independent); the option axis is kept only for suite uniformity.
 * - `@SealedCopy` + `@CopyToChildren` on one type — cross-feature, belongs in `MultipleDiagnosticsTest`.
 */
internal class SealedCopySnapshotTest :
    FunSpec({
        context("All patterns") {
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

                    test(testCaseName!!) {
                        runCompileSnapshotTest(input = scenario.toFileSpec(), options = creamOptions)
                    }
                }
        }
    })
