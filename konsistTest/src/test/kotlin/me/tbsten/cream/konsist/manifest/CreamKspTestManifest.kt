package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.FileContentManifest
import me.tbsten.cream.konsist.dsl.Manifest
import me.tbsten.cream.konsist.dsl.fileContentManifest
import me.tbsten.cream.konsist.dsl.manifest

/*
 * cream-ksp `test` source set の manifest（存在してよい .kt の目録 + トップレベル宣言の目録）。
 * 配置と中身の両方の正本。deny by default は両方に効く: ここに列挙されていない .kt を置くこと、
 * 列挙されていないトップレベル宣言を書くことは、どちらも違反になる
 * （me.tbsten.cream.konsist.CreamKspTestManifestTest が検出する）。
 * レイアウトの散文規約は .claude/rules/ksp-test.md（feature 5 種 / scenario / testing 基盤）。
 *
 * - このファイルは「データ」。DSL の実装・バグ修正は `dsl/` 側で行う
 * - 全ディレクトリを閉じている（`anyFiles()` は不使用・235 ファイル全列挙）。
 *   ファイルを足す・移すには人間による manifest の更新が要る。これは意図どおり（Q18）
 * - 明示的な緩和は `privateTopLevels()` のみ。テストケース・シナリオの追加が private の
 *   補助宣言（fixture・省略形）の追加として起きる場所に限って使い、理由を各所に書く。
 *   testing/（基盤）は private まで全列挙する（`CreamKspTestingManifest.kt`）
 * - 規約とのズレは TODO 付きの明示的な例外（baseline）としてこのファイルに現れる:
 *   1. childOptionals / parentOptional は 5 種のうち 3 種のみ（missingTestKinds）
 *   2. parentOptional/scenario/DeprecatedPropagation.kt の公開関数名（scenarioFamilyFunNameOverrides）
 *   3. 行数上限の個別超過（各 file エントリの maxLines）
 * - ルールを緩めたい・ファイルや宣言を足したいときは人間がこのファイルを編集する
 *   （将来 Claude Code hook で Write/Edit を deny する予定 — .local/.../adr/0002）
 */

internal fun creamKspTestManifest(): Manifest =
    manifest {
        module(":cream-ksp").sourceSet("test").dir("kotlin/me/tbsten/cream/ksp") {
            // --- root 直下: feature 横断の diagnostic / option 系 spec のみ ---
            requiredFile("AllKotlinFilesTest.kt") { assertContent(specFileContent("AllKotlinFilesTest")) }
            requiredFile("MultipleDiagnosticsTest.kt") { assertContent(specFileContent("MultipleDiagnosticsTest")) }
            requiredFile("OptionsDiagnosticTest.kt") { assertContent(specFileContent("OptionsDiagnosticTest")) }
            requiredFile("DefaultVisibilityOptionTest.kt") { assertContent(specFileContent("DefaultVisibilityOptionTest")) }
            requiredFile("ValueClassMappingOptionTest.kt") { assertContent(specFileContent("ValueClassMappingOptionTest")) }

            // --- options: CreamOptions パースの純ロジックテスト ---
            dir("options") {
                requiredFile("CreamOptionsParsingTest.kt") { assertContent(specFileContent("CreamOptionsParsingTest")) }
            }

            // --- core: レイヤリング Konsist + KSP 型に依存しない純ロジックテスト ---
            dir("core") {
                requiredFile("ArchTest.kt") { assertContent(specFileContent("ArchTest")) }
                dir("common") {
                    requiredFile("CopyFunctionNameTest.kt") { assertContent(specWithPrivateHelpersFileContent("CopyFunctionNameTest")) }
                    requiredFile("FunctionNameTemplateTest.kt") { assertContent(specWithPrivateHelpersFileContent("FunctionNameTemplateTest")) }
                }
            }

            // --- feature: 注釈ごとの 5 種テスト + scenario/（curated case） ---
            dir("feature") {
                requiredFile("ArchTest.kt") { assertContent(specFileContent("ArchTest")) }
                FEATURES.forEach { feature ->
                    val feat = feature.upperCamelCase()
                    dir(feature) {
                        testKinds().forEach { kind ->
                            val specName = "$feat${kind}Test"
                            if (kind in missingTestKinds()[feature].orEmpty()) {
                                // TODO(#127 / ksp-test.md の 5 種規約とのズレ): このテスト種はまだ存在しない
                                //  （childOptionals / parentOptional は 3 種のみ）。file エントリなので実装は
                                //  いつでも置けるが、実装したら requiredFile に昇格して欠落を検出させること。
                                file("$specName.kt") { assertContent(specFileContent(specName)) }
                            } else {
                                requiredFile("$specName.kt") {
                                    if (specName == "ParentOptionalInvalidUsageTest") {
                                        // parentOptional は不正利用の診断ケースが多く 377 行ある。
                                        // TODO: ケースを分割できるなら 300 行以内に戻し、この例外を消す
                                        maxLines(400)
                                    }
                                    assertContent(specFileContent(specName))
                                }
                            }
                        }
                        dir("scenario") {
                            scenarioFamilies().getValue(feature).forEach { family ->
                                val funName =
                                    scenarioFamilyFunNameOverrides()["$feature/$family"]
                                        ?: (family.lowerCamelCase() + "Scenarios")
                                requiredFile("$family.kt") { assertContent(scenarioFamilyFileContent(funName)) }
                            }
                            requiredFile("Utils.kt") {
                                assertContent(scenarioSupportFileContent(scenarioUtilsFunctions().getValue(feature)))
                            }
                            scenarioUseCaseFunctions()[feature]?.let { useCaseFunNames ->
                                requiredFile("UseCases.kt") { assertContent(scenarioSupportFileContent(useCaseFunNames)) }
                            }
                        }
                    }
                }
            }

            // --- testing: feature 非依存のテスト基盤（compile / snapshot / poet / generator / …） ---
            testingInfrastructureDir()
        }
    }

/** kotest spec ファイルの形: `internal class <specName> : FreeSpec` ちょうど 1 つ。 */
internal fun specFileContent(specName: String): FileContentManifest = fileContentManifest { topLevelClass(specName) }

/**
 * private の補助宣言（fixture・省略形）を持つ kotest spec ファイルの形。
 * テストケースの追加が private ヘルパの追加として起きるため、private は個数・名前を固定しない
 * （公開面が spec クラス 1 つであることは固定する）。
 */
internal fun specWithPrivateHelpersFileContent(specName: String): FileContentManifest =
    fileContentManifest {
        topLevelClass(specName)
        privateTopLevels()
    }

/**
 * `scenario/<Family>.kt` の形: `internal fun <family>Scenarios(): Generator<SnapshotScenario>`
 * ちょうど 1 つだけを公開する（実測 122/122）。curated case の追加はファイル内の private
 * ヘルパ追加として起きるため、private は個数・名前を固定しない。
 */
private fun scenarioFamilyFileContent(scenariosFunName: String): FileContentManifest =
    fileContentManifest {
        topLevelFunction(scenariosFunName)
        privateTopLevels()
    }

/**
 * `scenario/Utils.kt` / `scenario/UseCases.kt` の形: 列挙した internal fun を公開する。
 * シナリオ追加に伴う private の補助宣言は個数・名前を固定しない。
 */
private fun scenarioSupportFileContent(internalFunNames: List<String>): FileContentManifest =
    fileContentManifest {
        internalFunNames.forEach { topLevelFunction(it) }
        privateTopLevels()
    }

/** 各 feature が持つ 5 種のテスト（`.claude/rules/ksp-test.md` のレイアウト規約）。 */
private fun testKinds(): List<String> = listOf("BasicUsage", "InvalidUsage", "EdgeUsage", "Property", "Snapshot")

/**
 * TODO(#127 / ksp-test.md の 5 種規約とのズレ): まだ実装されていないテスト種（baseline）。
 *  実装したらここから外して requiredFile に昇格させること。
 */
private fun missingTestKinds(): Map<String, Set<String>> =
    mapOf(
        "childOptionals" to setOf("EdgeUsage", "Property"),
        "parentOptional" to setOf("EdgeUsage", "Property"),
    )

/**
 * `scenario/<Family>.kt` の目録（feature → family 名のリスト。`Utils.kt` / `UseCases.kt` は含まない）。
 * family を足す・消す・改名するときは人間がこの一覧を更新する。
 */
private fun scenarioFamilies(): Map<String, List<String>> =
    mapOf(
        "copyTo" to
            listOf(
                "Constructor",
                "Deprecated",
                "Escaping",
                "Exclude",
                "FunName",
                "Generics",
                "Kdoc",
                "Map",
                "Matching",
                "Nesting",
                "PropertyShape",
                "SourceKind",
                "TargetKind",
                "ValueClassMapping",
                "Visibility",
            ),
        "copyFrom" to
            listOf(
                "Constructor",
                "Exclude",
                "FunName",
                "Generics",
                "Kdoc",
                "Map",
                "Matching",
                "Nesting",
                "PropertyShape",
                "SourceKind",
                "TargetKind",
                "ValueClassMapping",
                "Visibility",
            ),
        "copyToChildren" to
            listOf(
                "Exclude",
                "FunName",
                "Generics",
                "HierarchyShape",
                "Kdoc",
                "Map",
                "NotCopyToObject",
                "PropertyShape",
                "SealedParentKind",
                "ValueClassMapping",
                "Visibility",
            ),
        "sealedCopy" to
            listOf(
                "Exclude",
                "FunName",
                "Generics",
                "HierarchyShape",
                "Kdoc",
                "Map",
                "NonCopyableStrategy",
                "PropertyShape",
                "Repeatable",
                "SealedParentKind",
                "Visibility",
            ),
        "combineTo" to
            listOf(
                "Deprecated",
                "Exclude",
                "FunName",
                "Generics",
                "Kdoc",
                "Map",
                "Matching",
                "MultiSource",
                "PropertyShape",
                "SourceKind",
                "TargetKind",
                "ValueClassMapping",
                "Visibility",
            ),
        "combineFrom" to
            listOf(
                "Exclude",
                "FunName",
                "Generics",
                "Kdoc",
                "Map",
                "Matching",
                "MultiSource",
                "PropertyShape",
                "Repeatable",
                "SourceKind",
                "TargetKind",
                "ValueClassMapping",
                "Visibility",
            ),
        "copyMapping" to
            listOf(
                "CanReverse",
                "Constructor",
                "Excludes",
                "FunName",
                "Generics",
                "Kdoc",
                "Map",
                "Matching",
                "Nesting",
                "PropertyShape",
                "Repeatable",
                "SourceKind",
                "TargetKind",
                "ValueClassMapping",
                "Visibility",
            ),
        "combineMapping" to
            listOf(
                "Excludes",
                "FunName",
                "Generics",
                "Kdoc",
                "Map",
                "Matching",
                "MultiSource",
                "PropertyShape",
                "Repeatable",
                "SourceKind",
                "SourceKindValidation",
                "TargetKind",
                "ValueClassMapping",
                "Visibility",
            ),
        "parentOptional" to
            listOf(
                "DeprecatedPropagation",
                "Generics",
                "HierarchyShape",
                "Kdoc",
                "Merge",
                "PropertyName",
                "PropertyShape",
                "SealedParentKind",
                "Visibility",
            ),
        "childOptionals" to
            listOf(
                "Exclude",
                "HierarchyShape",
                "Kdoc",
                "ParentOptionalInterop",
                "PropertyFiltering",
                "PropertyName",
                "SealedParentKind",
                "Visibility",
            ),
    )

/**
 * TODO(命名規約とのズレ): `<family>Scenarios` はファイル名の lowerCamelCase 由来（実測 121/122）だが、
 *  parentOptional/scenario/DeprecatedPropagation.kt だけ `deprecatedScenarios` を公開している
 *  （期待形は `deprecatedPropagationScenarios`）。リネームしたらこの override を消すこと。
 */
private fun scenarioFamilyFunNameOverrides(): Map<String, String> = mapOf("parentOptional/DeprecatedPropagation" to "deprecatedScenarios")

/** `scenario/Utils.kt` が公開する internal fun の目録（feature ごとに共有 fixture が異なる）。 */
private fun scenarioUtilsFunctions(): Map<String, List<String>> =
    mapOf(
        "copyTo" to listOf("withCopyTo", "copyTo"),
        "copyFrom" to listOf("withCopyFrom", "copyFrom"),
        "copyToChildren" to listOf("withCopyToChildren", "copyToChildren", "sealedInterfaceParent", "childClass", "objectChild"),
        "sealedCopy" to listOf("withSealedCopy", "sealedCopy", "sealedInterfaceParent", "childClass", "objectChild"),
        "combineTo" to listOf("withCombineTo", "combineTo", "combinedInto"),
        "combineFrom" to listOf("withCombineFrom", "combineFrom"),
        "copyMapping" to listOf("mappingHolder", "withCopyMapping", "copyMapping"),
        "combineMapping" to listOf("mappingHolder", "withCombineMapping", "combineMapping"),
        "parentOptional" to
            listOf(
                "parentOptional",
                "parentOptionalProp",
                "defaultAccessorPropertyNamePlus",
                "sealedInterfaceParent",
                "childClass",
                "objectChild",
                "nestedSealed",
            ),
        "childOptionals" to
            listOf(
                "withChildOptionals",
                "childOptionals",
                "defaultAccessorPropertyNamePlus",
                "sealedInterfaceParent",
                "childClass",
                "objectChild",
                "nestedSealed",
            ),
    )

/**
 * `scenario/UseCases.kt` が公開する `internal fun <題材>UseCase(): SnapshotScenario` の目録。
 * doc/use-case/ の利用例と対応する（ksp-test.md「UseCase snapshot cases」）。
 * UseCases.kt を持たない feature はこの map に載せない。
 */
private fun scenarioUseCaseFunctions(): Map<String, List<String>> =
    mapOf(
        "copyTo" to listOf("itemDetailTransitionsUseCase"),
        "copyFrom" to listOf("itemDetailTransitionsFromTargetsUseCase"),
        "copyToChildren" to listOf("checkoutStateMachineUseCase", "counterReducerUseCase", "komaSearchStateUseCase"),
        "sealedCopy" to listOf("feedRefreshUseCase", "counterSharedContextUseCase"),
        "copyMapping" to listOf("layerModelMappingUseCase"),
    )

private fun String.upperCamelCase(): String = replaceFirstChar { it.uppercaseChar() }

private fun String.lowerCamelCase(): String = replaceFirstChar { it.lowercaseChar() }
