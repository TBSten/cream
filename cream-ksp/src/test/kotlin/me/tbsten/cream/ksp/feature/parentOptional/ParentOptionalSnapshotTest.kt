package me.tbsten.cream.ksp.feature.parentOptional

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.parentOptional.scenario.deprecatedScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.hierarchyShapeScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.mergeScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.propertyNameScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.sealedParentKindScenarios
import me.tbsten.cream.ksp.feature.parentOptional.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@ParentOptional` (child property → nullable extension accessor on
 * every transitive sealed ancestor). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - Misuse diagnostics (no sealed parent / private property / merged type mismatch / parent member
 *   collision / same-child duplicate merge) — golden-pinned in `ParentOptionalInvalidUsageTest`. The
 *   only snapshot reject here is `generics/chainedTypeParamRejected`, because the direct-pin-vs-chain
 *   boundary is the v1 generics contract this family defines.
 * - No funName / map / exclude / repeatable families — the annotation has none of those arguments and
 *   is not repeatable (`propertyName` is its naming axis and has its own family).
 * - No targetKind / nesting / constructor / matching families — there is no referenced target class,
 *   no constructor call, and no property matching (the accessor reads a single property); receiver
 *   FQ-name rendering is exercised by the nested hierarchy cases.
 * - `@ChildOptionals`-annotated ancestors (ownership rule) — snapshot-pinned from the other side in
 *   `ChildOptionalsSnapshotTest`'s `parentOptionalInterop` family; the suppression itself is
 *   example-tested in `ParentOptionalBasicUsageTest`.
 * - Options other than `defaultVisibility` (prefix / strategy / escapeDot / notCopyToObject) do not
 *   affect accessor generation — the representative option variants pin that invariance byte-identically.
 */
internal class ParentOptionalSnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sealedParentKind" case sealedParentKindScenarios()
                        "hierarchyShape" case hierarchyShapeScenarios()
                        "merge" case mergeScenarios()
                        "propertyName" case propertyNameScenarios()
                        "propertyShape" case propertyShapeScenarios()
                        "generics" case genericsScenarios()
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
                        "deprecated" case deprecatedScenarios()
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
