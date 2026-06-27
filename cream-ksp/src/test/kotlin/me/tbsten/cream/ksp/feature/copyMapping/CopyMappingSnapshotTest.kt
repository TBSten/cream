package me.tbsten.cream.ksp.feature.copyMapping

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.copyMapping.scenario.canReverseScenarios
import me.tbsten.cream.ksp.feature.copyMapping.scenario.constructorScenarios
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
import me.tbsten.cream.ksp.testing.poet.toFileSpec

/**
 * Golden snapshot coverage for `@CopyMapping` (holder-annotated library 1↔1 copy). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `@Exclude` — `@CopyMapping` has none (source/target are external classes you cannot annotate).
 * - `typealias` source/target — `SnapshotScenario` can't carry a `TypeAliasSpec`; covered by integration `TypeAliasTest`.
 * - cross-package `@Repeatable` multi-file fan-out (`groupBy { sourceClass.packageName }` → N files) — needs the
 *   multi-`FileSpec` overload (single `GENERATED_PACKAGE` here); `repeatable/multipleAnnotations` covers same-package.
 * - one-bad-annotation-suppresses-the-holder short-circuit + missing source/target diagnostics — pure misuse
 *   diagnostics, belong in `CopyMappingInvalidUsageTest`.
 * - `canReverse` × sealed target — both feed one `generatesMultipleFunctions` boolean and are covered individually
 *   (`canReverse` family + `targetKind/sealedInterfaceTarget`); they share the multi-fn reject path.
 * - #144 sealed-target KDoc misattribution (attributes generation to the source class instead of the holder) is
 *   FROZEN as a known-quirk golden, not patched.
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
