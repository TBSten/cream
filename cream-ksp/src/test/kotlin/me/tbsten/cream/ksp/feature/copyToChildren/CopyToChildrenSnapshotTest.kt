package me.tbsten.cream.ksp.feature.copyToChildren

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.checkoutStateMachineUseCase
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.counterReducerUseCase
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.hierarchyShapeScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.komaSearchStateUseCase
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.notCopyToObjectScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.sealedParentKindScenarios
import me.tbsten.cream.ksp.feature.copyToChildren.scenario.visibilityScenarios
import me.tbsten.cream.ksp.options.CreamOptions
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
 * - `notCopyToObject` option-fallback (arg unset + `cream.notCopyToObject=true`) is now pinned by the
 *   "UseCase/CheckoutUiState nested state machine (notCopyToObject)" case below — and it FALSIFIED the
 *   earlier assumption that it is byte-identical to `notCopyToObject/objectChildSuppressed` (arg=true):
 *   the option is currently ignored (the object copy function is still generated; the annotation's default
 *   `false` shadows the option under KSP2). The golden captures that current behavior — a generator bug to
 *   fix separately, together with the golden.
 * - targetKind reject arms other than enum (annotation-class is impossible for a sealed leaf; non-sealed-iface /
 *   abstract / inner / private-ctor leaves flow through copyTo's shared dispatch byte-identically) — one
 *   representative reject (enum-child) suffices.
 * - non-data (plain) class leaf and `data object` vs plain `object` leaf — same constructor-call / OBJECT path
 *   as the covered data-class / object leaves (byte-identical).
 * - No `@Repeatable` family — `@CopyToChildren` is not repeatable.
 * - `@CopyToChildren` + `@SealedCopy` on one type — cross-feature, belongs in `MultipleDiagnosticsTest`.
 *
 * UseCase cases pin the doc/use-case examples with default options:
 * - "UseCase/CheckoutUiState nested state machine" (+ its notCopyToObject variant, the option the doc's Notes
 *   call out for `Idle`) ← doc/use-case/ui-state-management-by-sealed-class/03.md (undefined `CartItem` /
 *   `Address` / `PaymentMethod` supplied as minimal stubs).
 * - "UseCase/CounterUiState MVI reducer transitions" ←
 *   doc/use-case/ui-state-management-by-sealed-class/04.md; the doc combines `@CopyToChildren` + `@SealedCopy`
 *   on one type, split here per annotation (the `@SealedCopy` half lives in `SealedCopySnapshotTest`).
 * - "UseCase/SearchState Koma store" ← doc/use-case/ui-state-management-by-sealed-class/05.md (Koma's `State`
 *   stubbed as an empty interface; the children the doc elides after adding `sortOrder` / `searchedAt` are
 *   completed so the input compiles).
 */
internal class CopyToChildrenSnapshotTest :
    FreeSpec({
        "UseCase" - {
            "CheckoutUiState nested state machine" {
                runCompileSnapshotTest(inputs = checkoutStateMachineUseCase().files, options = CreamOptions.default)
            }

            "CheckoutUiState nested state machine (notCopyToObject)" {
                runCompileSnapshotTest(
                    inputs = checkoutStateMachineUseCase().files,
                    options = CreamOptions.default.copy(notCopyToObject = true),
                )
            }

            "CounterUiState MVI reducer transitions" {
                runCompileSnapshotTest(inputs = counterReducerUseCase().files, options = CreamOptions.default)
            }

            "SearchState Koma store" {
                runCompileSnapshotTest(inputs = komaSearchStateUseCase().files, options = CreamOptions.default)
            }
        }

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
                        "map" case mapScenarios()
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
