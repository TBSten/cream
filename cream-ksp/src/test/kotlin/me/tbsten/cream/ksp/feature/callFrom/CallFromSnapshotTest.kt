package me.tbsten.cream.ksp.feature.callFrom

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.ksp.feature.callFrom.scenario.deprecatedShapeScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.excludeScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.extensionScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.funNameScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.functionKindScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.genericsScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.kdocScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.mapScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.matchingScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.modifiersScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.multiSourceScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.nestingScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.propertyShapeScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.returnShapeScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.signatureShapeScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.sourceKindScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.typealiasShapeScenarios
import me.tbsten.cream.ksp.feature.callFrom.scenario.visibilityScenarios
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.cream.validCreamOptions
import me.tbsten.cream.ksp.testing.generator.util.cartesian
import me.tbsten.cream.ksp.testing.generator.util.union

/**
 * Golden snapshot coverage for `@CallFrom` (function-annotated bridge overload, N sources -> N
 * overloads of one function). See `.claude/skills/cream-snapshot-test`.
 *
 * Intentionally NOT covered as snapshot cases (and why):
 * - `targetKind` family — `@CallFrom` has no target class; the ClassKind dispatch does not apply.
 *   Recast as `functionKind` (top-level / member / object member / companion member).
 * - `constructor` family — the bridge target is a single function, there is no per-constructor
 *   loop; `vararg` moved into `signatureShape`.
 * - `repeatable` family — `@CallFrom` is not `@Repeatable`. (`funName` IS supported — family
 *   `17--funName` — but as a plain literal only: `@CallFrom` has no target class, so the
 *   `CopyTarget*` naming tokens do not apply.)
 * - unsupported function kinds (private / protected / local / abstract / expect / member
 *   extension / member of a generic class / `ERROR`-deprecated), the annotation-shape errors
 *   (empty or duplicated sources, bridge-parameter name collision), visibility violations, and
 *   bridge-signature collisions — diagnostic goldens in `CallFromInvalidUsageTest`.
 * - context parameters on the annotated function — invisible to the KSP API (no representation
 *   as of KSP 2.2.20-2.0.4), so no scenario (nor diagnostic) can observe them; documented
 *   limitation in `doc/call-from.md`.
 * - byte-identity cross-check against copyTo/copyFrom — `@CallFrom` has its own core generator
 *   (`appendCallFromFunction`, function signature instead of constructor call), so there is no
 *   structural twin; substituted by behavioral spot-checks (`= <sourceParam>.<prop>` defaults,
 *   parameter forwarding in the body) over the goldens.
 */
internal class CallFromSnapshotTest :
    FreeSpec({
        "All patterns" - {
            cartesian(
                union {
                    withNumberPrefix(length = 2) {
                        "sourceKind" case sourceKindScenarios()
                        "functionKind" case functionKindScenarios()
                        "nesting" case nestingScenarios()
                        "generics" case genericsScenarios()
                        "signatureShape" case signatureShapeScenarios()
                        "propertyShape" case propertyShapeScenarios()
                        "matching" case matchingScenarios()
                        "multiSource" case multiSourceScenarios()
                        "map" case mapScenarios()
                        "exclude" case excludeScenarios()
                        "kdoc" case kdocScenarios()
                        "visibility" case visibilityScenarios()
                        "extension" case extensionScenarios()
                        "modifiers" case modifiersScenarios()
                        "returnShape" case returnShapeScenarios()
                        "typealiasShape" case typealiasShapeScenarios()
                        "deprecated" case deprecatedShapeScenarios()
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
