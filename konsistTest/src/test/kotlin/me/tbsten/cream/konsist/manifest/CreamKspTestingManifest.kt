package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.ManifestEntriesBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Private
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Public

/*
 * cream-ksp `test` source set のうち `testing/`（feature 非依存のテスト基盤）の目録。
 * `CreamKspTestManifest.kt` の分冊（1 ファイルが大きくなりすぎるため区画で分けた）。
 * 同じく「データ」であり、編集ルールも CreamKspTestManifest.kt の冒頭コメントに従う。
 *
 * 基盤は private の補助宣言まで全列挙する（scenario 系と違い、ヘルパの追加・改名は
 * 基盤の設計変更なので人間の判断を通す）。共有ヘルパを足すときは manifest の更新と併せて
 * .claude/rules/ksp-test.md の「レイアウト」も更新すること。
 */
internal fun ManifestEntriesBuilder.testingInfrastructureDir() {
    dir("testing") {
        // --- compile: compileWithCream（kctfork + KSP2）と snapshot 実行のラッパー ---
        dir("compile") {
            requiredFile("CreamCompilation.kt") {
                assertContent {
                    topLevelClass("CreamSourcesBuilder")
                    // ソース列挙の 2 形（vararg / builder）
                    topLevelFunction("compileWithCream", count = 2)
                    topLevelFunction("runCompilation", visibility = Private)
                    topLevelProperty("creamCompilationClasspath", visibility = Private)
                    topLevelFunction("classpathRoot", visibility = Private)
                    topLevelClass("TeeOutputStream", visibility = Private)
                }
            }
            requiredFile("CreamCompilationResult.kt") {
                assertContent { topLevelClass("CreamCompilationResult") }
            }
            requiredFile("CreamCompilationResultUtils.kt") {
                assertContent {
                    topLevelFunction("generatedSourceText")
                    topLevelFunction("kspSourcesDir")
                    topLevelFunction("normalizedCompilerOutput")
                }
            }
            requiredFile("CreamCompilationUtils.kt") {
                assertContent {
                    // options 引数の有無の 2 オーバーロード
                    topLevelFunction("runCompileSnapshotTest", count = 2)
                    topLevelFunction("toKspArgs")
                }
            }
        }

        // --- snapshot: golden 比較（facet 形式） ---
        dir("snapshot") {
            requiredFile("SnapshotAssertion.kt") {
                assertContent {
                    topLevelProperty("updateSnapshots", visibility = Private)
                    topLevelProperty("snapshotRoot", visibility = Private)
                    topLevelFunction("snapshotRelativePath", visibility = Private)
                    // トップレベル形 / TestScope 拡張形の 2 つ
                    topLevelFunction("assertMatchesSnapshot", count = 2)
                    topLevelInterface("SnapshotFacetBuilder")
                    topLevelClass("SnapshotFacetBuilderImpl", visibility = Private)
                    topLevelClass("Facet", visibility = Private)
                    topLevelFunction("renderFacets", visibility = Private)
                    topLevelFunction("renderFencedBlock", visibility = Private)
                }
            }
        }

        // --- poet: 入力ソースのビルダー（kotlinpoet ラッパー） ---
        dir("poet") {
            requiredFile("ClassBuilders.kt") {
                assertContent {
                    topLevelFunction("classNameOf")
                    topLevelClass("Prop")
                    topLevelFunction("clazz")
                    topLevelFunction("dataClass")
                    topLevelFunction("sealedInterface")
                    topLevelFunction("valueClass")
                    topLevelFunction("asInner")
                    topLevelFunction("containing")
                    topLevelFunction("classWithNested")
                }
            }
            requiredFile("SnapshotScenario.kt") {
                assertContent {
                    topLevelClass("SnapshotScenario")
                    // data class と同名の factory fun（List / vararg の 2 形）
                    topLevelFunction("SnapshotScenario", count = 2)
                    topLevelFunction("inputFileSpec", count = 2)
                    topLevelFunction("plusDeclarations")
                    topLevelFunction("snapshotScenarios")
                }
            }
            requiredFile("SnapshotScenarioTest.kt") { assertContent(specFileContent("SnapshotScenarioTest")) }
        }

        // --- generator: generated case 基盤（Generator と合成コンビネータ） ---
        dir("generator") {
            requiredFile("Generator.kt") {
                assertContent {
                    topLevelInterface("Generator")
                    topLevelClass("GeneratorValue")
                }
            }
            requiredFile("GeneratorBuilder.kt") {
                assertContent {
                    topLevelFunction("generator")
                    topLevelInterface("GeneratorBuilder")
                    topLevelClass("GeneratorBuilderImpl", visibility = Private)
                }
            }
            requiredFile("ArbToGenerator.kt") {
                // 代表値の渡し方（Sequence / vararg）の 2 オーバーロード
                assertContent { topLevelFunction("toGenerator", count = 2) }
            }
            dir("clazz") {
                requiredFile("BasicTypeGenerator.kt") {
                    assertContent { topLevelFunction("basicType") }
                }
                requiredFile("ClassGenerator.kt") {
                    assertContent {
                        topLevelFunction("classSpec")
                        topLevelFunction("dataClassSpec")
                        topLevelFunction("objectSpec")
                        topLevelFunction("asPrimaryConstructor")
                        topLevelFunction("withMembers")
                        topLevelFunction("withPrimaryConstructor", visibility = Private)
                        topLevelFunction("asSecondaryConstructor", visibility = Private)
                        topLevelFunction("withBodyProperties", visibility = Private)
                    }
                }
                requiredFile("EnumGenerator.kt") {
                    assertContent {
                        topLevelProperty("ENUM_CONSTANTS", visibility = Private)
                        topLevelFunction("enumSpec")
                        topLevelFunction("enumConstant", visibility = Private)
                    }
                }
                requiredFile("InterfaceGenerator.kt") {
                    assertContent { topLevelFunction("interfaceSpec") }
                }
                requiredFile("MemberGenerator.kt") {
                    assertContent {
                        topLevelFunction("property")
                        topLevelFunction("function")
                    }
                }
                requiredFile("PropertiesGenerator.kt") {
                    assertContent {
                        topLevelFunction("properties")
                        topLevelFunction("propertyOf", visibility = Private)
                    }
                }
                requiredFile("RenderSource.kt") {
                    assertContent {
                        topLevelProperty("GENERATED_PACKAGE")
                        topLevelFunction("toKotlinSource")
                        topLevelFunction("toSource")
                    }
                }
                requiredFile("TypeBuilderGenerator.kt") {
                    assertContent {
                        topLevelEnumClass("TypeKind")
                        topLevelFunction("typeSpec")
                    }
                }
                requiredFile("TypeParametersGenerator.kt") {
                    assertContent {
                        topLevelFunction("typeParameters")
                        topLevelFunction("propertyOf", visibility = Private)
                    }
                }
                requiredFile("TypeVariables.kt") {
                    assertContent {
                        topLevelFunction("addDerivedTypeVariables")
                        topLevelFunction("collectTypeVariablesInto", visibility = Private)
                    }
                }
            }
            dir("cream") {
                requiredFile("CreamOptionsGenerator.kt") {
                    assertContent {
                        topLevelFunction("validCreamOptions")
                        topLevelFunction("defaultCreamOptionsOnly")
                        topLevelFunction("withDefaultNamingOptions", visibility = Private)
                        topLevelFunction("copyFunNamePrefix")
                        topLevelFunction("copyFunNamingStrategy")
                        topLevelFunction("escapeDot")
                        topLevelFunction("notCopyToObject")
                        topLevelFunction("defaultVisibility")
                        topLevelFunction("creamOptionsLabel", visibility = Private)
                        topLevelFunction("cases", visibility = Private)
                    }
                }
            }
            dir("util") {
                requiredFile("Combine.kt") {
                    assertContent {
                        // 2 / 3 generator の 2 オーバーロード
                        topLevelFunction("cartesian", count = 2)
                        topLevelFunction("pairedWith", visibility = Private)
                        // arity 2〜6 の 5 オーバーロード
                        topLevelFunction("combine", count = 5)
                        topLevelFunction("combineToList")
                        topLevelFunction("varyingOnePair")
                        topLevelFunction("combineToListVaryingOne")
                        topLevelFunction("toListValue", visibility = Private)
                        topLevelFunction("cartesianProduct", visibility = Private)
                    }
                }
                requiredFile("Constant.kt") {
                    assertContent { topLevelFunction("constant") }
                }
                requiredFile("ListGenerator.kt") {
                    assertContent { topLevelFunction("list") }
                }
                requiredFile("Map.kt") {
                    assertContent { topLevelFunction("map") }
                }
                requiredFile("MapLabel.kt") {
                    assertContent { topLevelFunction("mapLabel") }
                }
                requiredFile("OrNull.kt") {
                    assertContent { topLevelFunction("orNull") }
                }
                requiredFile("Union.kt") {
                    assertContent {
                        // List 拡張 / vararg / builder の 3 形
                        topLevelFunction("union", count = 3)
                        topLevelInterface("UnionBuilder")
                        topLevelInterface("NumberPrefixScope")
                        topLevelClass("UnionBuilderImpl", visibility = Private)
                    }
                }
                requiredFile("WithRepresentativeValues.kt") {
                    assertContent { topLevelFunction("withRepresentativeValues") }
                }
            }
        }

        // --- kotlincodelikestring: 値 → Kotlin コード風文字列の表示ヘルパ ---
        dir("kotlincodelikestring") {
            requiredFile("BuildKotlinCodeLikeString.kt") {
                assertContent { topLevelFunction("buildKotlinCodeLikeString", visibility = Public) }
            }
            requiredFile("ClassNameUtil.kt") {
                assertContent { topLevelProperty("underPackageClassName", visibility = Public) }
            }
            requiredFile("Fallback.kt") {
                assertContent { topLevelInterface("Fallback", visibility = Public) }
            }
            requiredFile("StringExt.kt") {
                assertContent {
                    topLevelFunction("escapeString")
                    topLevelFunction("escapeChar")
                    topLevelFunction("withPrefixEachLines")
                    topLevelProperty("KOTLIN_HARD_KEYWORDS", visibility = Private)
                    topLevelProperty("VALID_KOTLIN_IDENTIFIER", visibility = Private)
                    topLevelFunction("escapeKotlinIdentifierIfNeeded")
                }
            }
            requiredFile("Transform.kt") {
                assertContent {
                    topLevelInterface("Transform", visibility = Public)
                    // Transform 合成 / reified 型フィルタ付き合成の 2 形
                    topLevelFunction("transform", visibility = Public, count = 2)
                }
            }
            requiredFile("TransformBuiltIns.kt") {
                // 組み込み型（数値・文字列・Throwable・KClass …）ごとの変換の列挙で 321 行ある。
                // 型ごとの private フォーマッタの列挙が本体で、分割しても見通しは良くならない
                maxLines(350)
                assertContent {
                    topLevelFunction("transformBuiltIns")
                    topLevelFunction("buildMultiLineCall", visibility = Private)
                    topLevelFunction("emptyFuncFor", visibility = Private)
                    topLevelFunction("formatInt", visibility = Private)
                    topLevelFunction("formatLong", visibility = Private)
                    topLevelFunction("formatShort", visibility = Private)
                    topLevelFunction("formatByte", visibility = Private)
                    topLevelFunction("formatFloat", visibility = Private)
                    topLevelFunction("formatDouble", visibility = Private)
                    topLevelFunction("buildClassExpression", visibility = Private)
                    topLevelFunction("formatThrowable", visibility = Private)
                    topLevelFunction("formatKClass", visibility = Private)
                }
            }
            dir("cream") {
                requiredFile("CreamOptionsToKspConfigString.kt") {
                    assertContent { topLevelFunction("toKspConfigString") }
                }
            }
        }

        // --- smoke: 基盤自体が動くことの最小確認 ---
        dir("smoke") {
            requiredFile("ClazzGeneratorSmokeTest.kt") { assertContent(specFileContent("ClazzGeneratorSmokeTest")) }
            requiredFile("CreamCompilationSmokeTest.kt") { assertContent(specFileContent("CreamCompilationSmokeTest")) }
            requiredFile("GeneratorSmokeTest.kt") { assertContent(specFileContent("GeneratorSmokeTest")) }
            requiredFile("RunCompileSnapshotTestSmokeTest.kt") {
                assertContent(specWithPrivateHelpersFileContent("RunCompileSnapshotTestSmokeTest"))
            }
        }

        // --- konsist: cream-ksp 側 3 ArchTest の共有 scope・定数（manifest 3 で廃止予定） ---
        dir("konsist") {
            requiredFile("KonsistSupport.kt") {
                assertContent {
                    topLevelProperty("CREAM_ROOT")
                    topLevelProperty("KSP_ROOT")
                    topLevelProperty("UTIL_PACKAGE")
                    topLevelProperty("CORE_PACKAGE")
                    topLevelProperty("FEATURE_PACKAGE")
                    topLevelProperty("KSP_API_PACKAGE")
                    topLevelProperty("PROCESS_CONTEXT_TYPE")
                    topLevelProperty("MAX_FILE_LINES")
                    topLevelProperty("COMPOSITION_ROOT_TYPES")
                    topLevelProperty("FILE_LINE_LIMIT_OVERRIDES")
                    topLevelProperty("CORE_SUBPACKAGES")
                    topLevelProperty("ROOT_ALLOWED_FILES")
                    topLevelProperty("creamKspMain")
                    topLevelFunction("inLayer")
                    topLevelFunction("importsFrom")
                }
            }
        }
    }
}
