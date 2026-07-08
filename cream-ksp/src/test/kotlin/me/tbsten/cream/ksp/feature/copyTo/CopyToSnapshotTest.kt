package me.tbsten.cream.ksp.feature.copyTo

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.copyTo.scenario.constructorScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.deprecatedScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.escapingScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.matchingScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.nestingScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.sourceKindScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.targetKindScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.valueClassMappingScenarios
import me.tbsten.cream.ksp.feature.copyTo.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@CopyTo` (source-annotated 1→1 copy). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `typealias` source/target — the scenario factories take only `TypeSpec`, not `TypeAliasSpec`; alias
 *   resolution is generic and covered by integration `test/.../copyTo/TypeAliasTest.kt`. (Exception:
 *   `valueClassMapping/typealiasTarget` builds its `FileSpec` by hand to pin how the value-class wrap
 *   renders through an aliased parameter type.)
 * - `@CopyTo.Map` on a `TYPE_PARAMETER` — KotlinPoet can't render an annotation on a type-param declaration;
 *   the VALUE_PARAMETER form (the `map` family) covers the behavior.
 * - No `repeatable` / `multiSource` / `notCopyToObject` families — `@CopyTo` is not `@Repeatable`, is 1→1
 *   (no N sources/targets), and `notCopyToObject` is `@CopyToChildren`'s axis (`targetKind/objectTarget` pins
 *   the default object behavior).
 * - A standalone data-class `sourceKind` case — data classes are exercised throughout `propertyShape`.
 */
internal class CopyToSnapshotTest :
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
                        "deprecated" case deprecatedScenarios()
                        "escaping" case escapingScenarios()
                        "valueClassMapping" case valueClassMappingScenarios()
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
