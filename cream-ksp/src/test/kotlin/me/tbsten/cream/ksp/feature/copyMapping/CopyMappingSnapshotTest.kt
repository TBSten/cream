package me.tbsten.cream.ksp.feature.copyMapping

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.copyMapping.scenario.canReverseScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.constructorScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.excludesScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.matchingScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.nestingScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.repeatableScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.sourceKindScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.targetKindScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@CopyMapping` (holder-annotated library 1↔1 copy). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `@Exclude` — `@CopyMapping` has none (source/target are external classes you cannot annotate);
 *   the annotation-level `excludes` argument covers the same semantics (`excludes` family).
 * - `typealias` source/target — `SnapshotScenario` can't carry a `TypeAliasSpec`; covered by integration `TypeAliasTest`.
 * - cross-package emission (source/target in a different package than the holder) — the snapshot
 *   harness emits a single `GENERATED_PACKAGE`, so this is covered by `CopyMappingEdgeUsageTest`
 *   (issue #145: the copy function lands in the holder package, never the source package).
 * - one-bad-annotation-suppresses-the-holder short-circuit + missing source/target diagnostics — pure misuse
 *   diagnostics, belong in `CopyMappingInvalidUsageTest`.
 * - `canReverse` × sealed target — both feed one `generatesMultipleFunctions` boolean and are covered individually
 *   (`canReverse` family + `targetKind/sealedInterfaceTarget`); they share the multi-fn reject path.
 *
 * `targetKind/sealedInterfaceTarget` pins the sealed-target fan-out: each generated leaf copy function attributes
 * generation to the `@CopyMapping`-annotated holder (`[Mapping]`), not the source class, per issue #144.
 */
internal class CopyMappingSnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sourceKind" case sourceKindScenarios()
                        "targetKind" case targetKindScenarios()
                        "nesting" case nestingScenarios()
                        "generics" case genericsScenarios()
                        "constructor" case constructorScenarios()
                        "propertyShape" case propertyShapeScenarios()
                        "matching" case matchingScenarios()
                        "map" case mapScenarios()
                        "canReverse" case canReverseScenarios()
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
                        "funName" case funNameScenarios()
                        "repeatable" case repeatableScenarios()
                        "excludes" case excludesScenarios()
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
