package me.tbsten.cream.ksp.feature.copyFrom

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.copyFrom.scenario.constructorScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.matchingScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.nestingScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.sourceKindScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.targetKindScenarios
import me.tbsten.cream.ksp.feature.copyFrom.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@CopyFrom` (target-annotated 1→1 copy). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `typealias` source/target — `SnapshotScenario` holds only `List<TypeSpec>`, not `TypeAliasSpec`; covered by
 *   integration `test/.../copyFrom/TypeAliasTest.kt`.
 * - `@CopyFrom.Map` on a `TYPE_PARAMETER` — KotlinPoet can't render it; the VALUE_PARAMETER `map` family covers it.
 * - No `repeatable` / `multiSource` families — `@CopyFrom` is not `@Repeatable` and is 1→1.
 *
 * `targetKind/sealedInterfaceTarget` pins the sealed-target fan-out: each generated leaf copy function attributes
 * generation to the `@CopyFrom`-annotated target (`[Target]`), per issue #144.
 */
internal class CopyFromSnapshotTest :
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
                        "exclude" case excludeScenarios()
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
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
