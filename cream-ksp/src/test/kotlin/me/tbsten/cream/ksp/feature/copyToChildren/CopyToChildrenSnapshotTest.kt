package me.tbsten.cream.ksp.feature.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.hierarchyShapeScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.notCopyToObjectScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.sealedParentKindScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@CopyToChildren` (sealed parent → transitive concrete leaves fan-out).
 * See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `notCopyToObject` option-fallback (arg unset + `cream.notCopyToObject=true`) — output byte-identical to
 *   `notCopyToObject/objectChildSuppressed` (arg=true); the 3 representative options all set it false, and pinning
 *   it would need a shared options-axis change. The arg on/off axis itself IS covered.
 * - targetKind reject arms other than enum (annotation-class is impossible for a sealed leaf; non-sealed-iface /
 *   abstract / inner / private-ctor leaves flow through copyTo's shared dispatch byte-identically) — one
 *   representative reject (enum-child) suffices.
 * - non-data (plain) class leaf and `data object` vs plain `object` leaf — same constructor-call / OBJECT path
 *   as the covered data-class / object leaves (byte-identical).
 * - No `@Map` / `@Repeatable` families — `@CopyToChildren` has neither.
 * - `@CopyToChildren` + `@SealedCopy` on one type — cross-feature, belongs in `MultipleDiagnosticsTest`.
 */
internal class CopyToChildrenSnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sealedParentKind" case sealedParentKindScenarios()
                        "hierarchyShape" case hierarchyShapeScenarios()
                        "generics" case genericsScenarios()
                        "propertyShape" case propertyShapeScenarios()
                        "exclude" case excludeScenarios()
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
                        "notCopyToObject" case notCopyToObjectScenarios()
                        "funName" case funNameScenarios()
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
