package me.tbsten.cream.ksp.feature.childOptionals

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.childOptionals.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.childOptionals.scenario.hierarchyShapeScenarios
import me.tbsten.cream.ksp.feature.childOptionals.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.childOptionals.scenario.parentOptionalInteropScenarios
import me.tbsten.cream.ksp.feature.childOptionals.scenario.propertyFilteringScenarios
import me.tbsten.cream.ksp.feature.childOptionals.scenario.sealedParentKindScenarios
import me.tbsten.cream.ksp.feature.childOptionals.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@ChildOptionals` (sealed parent → nullable extension accessor for
 * every eligible property declared by its transitive concrete leaves). See
 * `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - Misuse diagnostics (non-sealed annotated class / merged type mismatch) — golden-pinned in
 *   `ChildOptionalsInvalidUsageTest`.
 * - No generics family — accessor type rendering is the exact shared `core/parentOptional` path
 *   pinned by `ParentOptionalSnapshotTest`'s `generics` family (byte-identical modulo attribution).
 * - No funName / map / repeatable families — the annotation has none of those arguments and is not
 *   repeatable. (`@ChildOptionals.Exclude` IS covered by the `exclude` family below.)
 * - No targetKind / nesting / constructor / matching families — no referenced target class, no
 *   constructor call, no property matching; receiver FQ-name rendering is exercised by the nested
 *   hierarchy cases.
 * - Accessor-less sweeps (object-only / leaf-less hierarchies) — produce an empty generated file via
 *   the same walk as `hierarchyShape`, with no unique branch worth pinning.
 */
internal class ChildOptionalsSnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sealedParentKind" case sealedParentKindScenarios()
                        "hierarchyShape" case hierarchyShapeScenarios()
                        "propertyFiltering" case propertyFilteringScenarios()
                        "parentOptionalInterop" case parentOptionalInteropScenarios()
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
                        "exclude" case excludeScenarios()
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
