package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.FileContentManifest
import me.tbsten.cream.konsist.dsl.Manifest
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Internal
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Private
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Public
import me.tbsten.cream.konsist.dsl.fileContentManifest
import me.tbsten.cream.konsist.dsl.manifest

/*
 * cream-ksp `main` source set の manifest（存在してよい .kt の目録 + トップレベル宣言の目録）。
 * 配置と中身の両方の正本。deny by default は両方に効く: ここに列挙されていない .kt を置くこと、
 * 列挙されていないトップレベル宣言を書くことは、どちらも違反になる
 * （me.tbsten.cream.konsist.CreamKspManifestTest が検出する）。
 *
 * - このファイルは「データ」。DSL の実装・バグ修正は `dsl/` 側で行う
 * - 中身は実態に厳密（宣言の種類 × 名前 × 可視性 × 個数まで固定）。private fun を 1 つ
 *   足すだけでも人間による manifest の更新が要る。これは意図どおり（Q18）
 * - 緩和は明示のみ: `privateTopLevels()` / `anyTopLevel()` を使う場所には必ず理由コメントを書く
 * - ルールを緩めたい・ファイルや宣言を足したいときは人間がこのファイルを編集する
 *   （将来 Claude Code hook で Write/Edit を deny する予定 — .local/.../adr/0002）
 * - 依存方向はこのファイルではなく me.tbsten.cream.konsist.CreamKspDependencyTest が検査する
 *   （正本は .claude/rules/ksp-architecture.md の依存方向テーブル）
 */

/**
 * feature ディレクトリ（= 注釈）の一覧。ディスクから発見せず列挙する
 * （発見にすると agent が feature を勝手に増やせてしまい deny by default が崩れるため）。
 */
internal val FEATURES =
    listOf(
        "copyTo",
        "copyFrom",
        "copyToChildren",
        "sealedCopy",
        "combineTo",
        "combineFrom",
        "copyMapping",
        "combineMapping",
        "parentOptional",
        "childOptionals",
    )

internal val creamKspManifest: Manifest =
    manifest {
        module(":cream-ksp").sourceSet("main").dir("kotlin/me/tbsten/cream/ksp") {
            // --- composition root: root 直下はこの 3 ファイルのみ ---
            requiredFile("CreamSymbolProcessor.kt") {
                assertContent { topLevelClass("CreamSymbolProcessor") }
            }
            requiredFile("CreamSymbolProcessorProvider.kt") {
                // cream-ksp/main で唯一の public（KSP のエントリポイント）
                assertContent { topLevelClass("CreamSymbolProcessorProvider", visibility = Public) }
            }
            requiredFile("ProcessContext.kt") {
                assertContent { topLevelClass("ProcessContext") }
            }

            // --- feature 層: 注釈ごとの入口。1 注釈 1 ディレクトリ ---
            FEATURES.forEach { feature ->
                requiredFile("feature/$feature/Process${feature.upperCamel()}.kt") {
                    assertContent(processFeatureFileContent())
                }
            }
            // 診断ヘルパの分割ファイル。実在するのは childOptionals のみで、実態に厳密に列挙する。
            // 旧 manifest は他 feature にも同名パターンで事前許可していたが、中身の grant を
            // 書けない架空ファイルの事前許可は deny by default と矛盾するため廃止した。
            // TODO: 他 feature に Diagnostics 分割ファイルが必要になったら、人間がその実態を列挙して足すこと。
            file("feature/childOptionals/ChildOptionalsDiagnostics.kt") {
                assertContent {
                    topLevelProperty("annotationName", visibility = Private)
                    topLevelFunction("warnChildOptionalsUnpinnedTypeParameters")
                    topLevelFunction("warnChildOptionalsExcludeHasNoEffect")
                    topLevelFunction("reportChildOptionalsNotADeclaration")
                    topLevelFunction("reportChildOptionalsNotSealed")
                }
            }

            // --- core 層: cream 固有の生成ロジック ---
            dir("core/common") {
                file("CodeGeneratorExt.kt") {
                    assertContent { topLevelFunction("createNewKotlinFile") }
                }
                file("CombineSourceAnnotation.kt") {
                    assertContent {
                        topLevelClass("CombineToSourceAnnotation")
                        topLevelClass("CombineFromSourceAnnotation")
                    }
                }
                file("CopyFunctionBodyParts.kt") {
                    assertContent {
                        topLevelFunction("appendTypeParameterList")
                        topLevelFunction("appendSourceClassWithTypeArgs")
                        topLevelFunction("appendTargetClassWithTypeArgs")
                        topLevelFunction("resolveConstructorParamTypeParameter")
                        topLevelFunction("appendWhereClause")
                        topLevelFunction("appendConstructorCallBody")
                    }
                }
                file("CopyFunctionName.kt") {
                    assertContent {
                        topLevelFunction("copyFunctionName")
                        topLevelClass("CopyFunctionName")
                    }
                }
                file("CopyFunctionNameExt.kt") {
                    assertContent {
                        topLevelFunction("toClassDeclarationInfo")
                        topLevelFunction("copyFunctionName")
                        topLevelFunction("resolveFunName")
                        topLevelFunction("resolveSealedCopyFunName")
                    }
                }
                file("CopyFunctionTypeParameters.kt") {
                    assertContent {
                        topLevelFunction("getCopyFunctionTypeParameters")
                        topLevelClass("CopyFunctionTypeParameters")
                        topLevelFunction("copyFunctionTypeParametersMap", visibility = Private)
                        topLevelClass("CopyFunctionTypeParameter")
                    }
                }
                file("CopySourceAnnotation.kt") {
                    assertContent {
                        topLevelClass("CopyToSourceAnnotation")
                        topLevelClass("CopyFromSourceAnnotation")
                        topLevelClass("CopyToChildrenSourceAnnotation")
                        topLevelClass("SealedCopySourceAnnotation")
                    }
                }
                file("DeclarationName.kt") {
                    assertContent {
                        // KSDeclaration / KClass<*> の 2 レシーバ分
                        topLevelProperty("fullName", count = 2)
                        topLevelProperty("underPackageName")
                    }
                }
                file("DeprecatedPropagation.kt") {
                    assertContent {
                        topLevelFunction("deprecatedAnnotationLine")
                        topLevelFunction("toAnnotationLine")
                        topLevelFunction("deprecatedAnnotationOrNull")
                        topLevelFunction("firstDeprecation", visibility = Private)
                        topLevelFunction("toKotlinStringLiteral", visibility = Private)
                    }
                }
                file("ExcludeProperty.kt") {
                    assertContent {
                        topLevelFunction("isExcludedFromCopy")
                        topLevelFunction("isSourcePropertyExcluded")
                        topLevelFunction("warnIfTargetExcludeHasNoEffect")
                        topLevelFunction("warnIfSourceExcludeHasNoEffect")
                        topLevelClass("MappingExcludesDirection")
                        topLevelFunction("warnIfMappingExcludesHaveNoEffect")
                        topLevelFunction("concreteTargets", visibility = Private)
                    }
                }
                file("FindMappedSourceProperty.kt") {
                    assertContent { topLevelTypeAlias("FindMappedSourceProperty") }
                }
                file("FindMatchedProperty.kt") {
                    // プロパティ照合（@Map / 名前一致 / デフォルト値）の分岐が密結合で、無理に割ると追いにくくなるため上限 500
                    maxLines(500)
                    assertContent {
                        topLevelFunction("findMatchedProperty")
                        topLevelFunction("findPropertyByNameResolution")
                        topLevelFunction("findSourcePropertyWithPropertyMappings")
                        topLevelFunction("findSourcePropertyWithMapAnnotationOnSource", visibility = Private)
                        topLevelFunction("findSourcePropertyWithCopyToMapAnnotation")
                        topLevelFunction("findSourcePropertyWithCombineToMapAnnotation")
                        topLevelFunction("findSourcePropertyWithCopyToChildrenMapAnnotation")
                        topLevelFunction("findSourcePropertyWithCombineFromMapAnnotationOnTarget")
                        topLevelFunction("findSourcePropertyWithCombineFromMapAnnotationOnSource")
                        topLevelFunction("findSourcePropertyWithCopyFromMapAnnotation")
                        topLevelFunction("findSourcePropertyByName", visibility = Private)
                        topLevelProperty("primitiveArrayByElement", visibility = Private)
                        topLevelFunction("matchesSourcePropertyType", visibility = Private)
                        topLevelFunction("isTypeCompatible", visibility = Private)
                    }
                }
                file("FunName.kt") {
                    assertContent { topLevelFunction("funNameTemplate") }
                }
                file("FunNameValidation.kt") {
                    assertContent {
                        topLevelFunction("invalidFunNameException", visibility = Private)
                        topLevelEnumClass("FunNameValidity")
                        topLevelProperty("isValid")
                        topLevelFunction("onInvalid")
                        topLevelFunction("validateFunName")
                    }
                }
                file("FunctionNameTemplate.kt") {
                    assertContent {
                        topLevelFunction("resolveFunNameTemplate")
                        topLevelFunction("containsAnyCopyFunNameToken")
                        topLevelFunction("pascalCase", visibility = Private)
                        topLevelFunction("snakeCase", visibility = Private)
                        topLevelProperty("copyTargetTokenExpanders", visibility = Private)
                    }
                }
                file("GenerateSourceAnnotation.kt") {
                    assertContent { topLevelInterface("GenerateSourceAnnotation") }
                }
                file("IsExcluded.kt") {
                    assertContent { topLevelTypeAlias("IsExcluded") }
                }
                file("KDoc.kt") {
                    assertContent {
                        topLevelFunction("appendAutoGeneratedFunctionKDoc")
                        topLevelClass("KDocAppender")
                        topLevelFunction("appendExample")
                        topLevelFunction("appendUserKDocBlock", visibility = Private)
                        topLevelFunction("autoGenerateKDoc", visibility = Private)
                    }
                }
                file("KSAnnotationExt.kt") {
                    assertContent {
                        topLevelFunction("annotationsOf")
                        topLevelFunction("classListArgument")
                        topLevelFunction("resolveClassListOrReport")
                        topLevelFunction("extractPropertyMappings")
                        topLevelFunction("extractExcludes")
                        topLevelFunction("extractKDoc")
                    }
                }
                file("MappingSourceAnnotation.kt") {
                    assertContent {
                        topLevelClass("CopyMappingSourceAnnotation")
                        topLevelClass("CombineMappingSourceAnnotation")
                    }
                }
                file("NotCopyToObject.kt") {
                    assertContent { topLevelFunction("notCopyToObject") }
                }
                file("OmitPackages.kt") {
                    assertContent { topLevelFunction("omitPackagesFor") }
                }
                file("ParentOptionalSourceAnnotation.kt") {
                    assertContent {
                        topLevelClass("ParentOptionalSourceAnnotation")
                        topLevelClass("ChildOptionalsSourceAnnotation")
                        topLevelFunction("parentOptionalPropertyNameOrNull", visibility = Private)
                        topLevelFunction("resolveAccessorName", visibility = Private)
                    }
                }
                file("ReportCreamError.kt") {
                    assertContent {
                        topLevelFunction("reportCreamError")
                        topLevelFunction("asDeclarationOrReport")
                        topLevelFunction("asClassDeclarationOrReport")
                    }
                }
                file("SealedTypeRendering.kt") {
                    assertContent {
                        topLevelFunction("renderWhenBranchType")
                        topLevelFunction("starProjectedTypeText", visibility = Private)
                        topLevelFunction("renderTypeParameterList")
                        topLevelFunction("renderWhereClause")
                        topLevelFunction("renderSealedReceiverType")
                    }
                }
                file("TargetValidation.kt") {
                    assertContent {
                        topLevelEnumClass("CopyTargetRejection")
                        topLevelFunction("concreteClassRejection")
                        topLevelFunction("constructorRejection", visibility = Private)
                    }
                }
                file("TypeAlias.kt") {
                    assertContent {
                        topLevelFunction("resolveToClassDeclaration")
                        topLevelFunction("unresolvableClassException", visibility = Private)
                        topLevelFunction("resolveClassDeclarationOrReport")
                    }
                }
                file("ValueClassConversionProbe.kt") {
                    assertContent {
                        topLevelProperty("MAX_VALUE_CLASS_CONVERSION_DEPTH", visibility = Private)
                        topLevelFunction("conversionOutcomeFor")
                        topLevelFunction("wrapOutcome", visibility = Private)
                        topLevelFunction("unwrapOutcome", visibility = Private)
                        topLevelFunction("inaccessibleConstructorNearMiss", visibility = Private)
                        topLevelFunction("asEligibleValueClass", visibility = Private)
                        topLevelFunction("valueClassUnderlyingParameter", visibility = Private)
                        topLevelFunction("hasAccessiblePrimaryConstructor", visibility = Private)
                        topLevelFunction("hasAccessibleUnderlyingProperty", visibility = Private)
                    }
                }
                file("ValueClassMapping.kt") {
                    assertContent {
                        topLevelInterface("ValueClassConversion")
                        topLevelInterface("ValueClassConversionOutcome")
                        topLevelFunction("defaultValueExpression")
                        topLevelFunction("findValueClassConversion")
                        topLevelFunction("findValueClassConversionOutcome")
                    }
                }
                file("Visibility.kt") {
                    assertContent {
                        topLevelProperty("visibilityStr")
                        topLevelFunction("toModifierString")
                        topLevelFunction("copyVisibilityArgument")
                    }
                }
            }
            dir("core/copyFun") {
                file("Class.kt") {
                    assertContent {
                        topLevelFunction("appendCopyToClassFunction")
                        topLevelFunction("appendCopyToClassKDoc", visibility = Private)
                    }
                }
                file("Object.kt") {
                    assertContent {
                        topLevelFunction("appendCopyToObjectFunction")
                        topLevelFunction("appendCopyToObjectKDoc", visibility = Private)
                    }
                }
                file("SealedClass.kt") {
                    assertContent { topLevelFunction("appendCopyToSealedClassFunction") }
                }
                file("Transform.kt") {
                    assertContent {
                        topLevelFunction("reportRejection", visibility = Private)
                        topLevelFunction("appendCopyFunction")
                    }
                }
            }
            dir("core/combineFun") {
                file("CombineExample.kt") {
                    assertContent {
                        topLevelFunction("lowerCamelName", visibility = Private)
                        topLevelFunction("appendCombineAutoDescription")
                        topLevelFunction("combineExampleBody")
                    }
                }
                file("CombineObject.kt") {
                    assertContent {
                        topLevelFunction("appendCombineToObjectFunction")
                        topLevelFunction("appendCombineToObjectKDoc", visibility = Private)
                    }
                }
                file("CombineToClass.kt") {
                    assertContent {
                        topLevelFunction("appendCombineToClassFunction")
                        topLevelFunction("defaultValueQualifier", visibility = Private)
                        topLevelFunction("appendCombineToClassKDoc", visibility = Private)
                    }
                }
                file("CombineTransform.kt") {
                    assertContent {
                        topLevelFunction("appendCombineToFunction")
                        topLevelFunction("reportUnsupportedCombineTarget", visibility = Private)
                    }
                }
            }
            dir("core/sealedCopy") {
                file("SealedCopy.kt") {
                    assertContent {
                        topLevelFunction("appendSealedCopyFunction")
                        topLevelFunction("appendSealedCopyHeader", visibility = Private)
                        topLevelFunction("appendSealedCopyBody", visibility = Private)
                    }
                }
                file("SealedCopyAssignability.kt") {
                    assertContent { topLevelFunction("isParameterAssignableFromProperty") }
                }
                file("SealedCopyDiagnostic.kt") {
                    assertContent { topLevelFunction("nonCopyableErrorException") }
                }
                file("SealedCopyKDoc.kt") {
                    assertContent { topLevelFunction("appendSealedCopyKDoc") }
                }
                file("SealedCopyLeaf.kt") {
                    assertContent {
                        topLevelInterface("SealedCopyLeaf")
                        topLevelClass("ArgumentBinding")
                        topLevelFunction("collectAbstractProperties")
                        topLevelFunction("viaFunctions")
                        topLevelFunction("mappedAbstractPropertyName")
                        topLevelFunction("classify")
                        topLevelFunction("toArgumentBindings", visibility = Private)
                        topLevelFunction("identityBindings", visibility = Private)
                        topLevelFunction("hasDelegatableCopy", visibility = Private)
                        topLevelFunction("findCompatibleCopyFunction", visibility = Private)
                    }
                }
                file("SealedCopyViaValidation.kt") {
                    assertContent {
                        topLevelClass("SealedCopyViaError")
                        topLevelFunction("collectSealedCopyViaErrors")
                    }
                }
            }
            dir("core/parentOptional") {
                file("ParentOptionalAccessor.kt") {
                    assertContent {
                        topLevelFunction("appendParentOptionalAccessor")
                        topLevelFunction("appendParentOptionalKDoc", visibility = Private)
                        topLevelFunction("deprecatedAnnotationLineOrNull", visibility = Private)
                    }
                }
                file("ParentOptionalAnnotationLookup.kt") {
                    assertContent {
                        topLevelFunction("parentOptionalAnnotationOrNull")
                        topLevelFunction("correspondingConstructorParameter")
                        // KSPropertyDeclaration / KSClassDeclaration の 2 レシーバ分
                        topLevelFunction("isAccessibleFromGeneratedAccessor", count = 2)
                        topLevelFunction("isExtensionProperty")
                        topLevelFunction("reportParentOptionalExtensionProperty")
                        topLevelFunction("firstInaccessibleClassOrNull")
                        topLevelFunction("classChain")
                    }
                }
                file("ParentOptionalEntryOrder.kt") {
                    assertContent { topLevelFunction("withEntriesMostDerivedFirst") }
                }
                file("ParentOptionalTarget.kt") {
                    assertContent {
                        topLevelClass("ParentOptionalEntry")
                        topLevelClass("ParentOptionalAccessorSpec")
                        topLevelFunction("validatedPropertyTypeTextOrNull")
                        topLevelProperty("isPropertyTypeNullable")
                        topLevelFunction("renderPropertyTypeOrNull", visibility = Private)
                        topLevelFunction("childTypeParamToParentName", visibility = Private)
                        topLevelFunction("typeParameterNamesUnpinnedBy")
                    }
                }
                file("ParentOptionalVisibility.kt") {
                    assertContent {
                        topLevelFunction("validatedVisibilityModifierOrNull")
                        topLevelFunction("inheritedNarrowestModifier", visibility = Private)
                        topLevelFunction("firstExposedInternalDeclarationOrNull", visibility = Private)
                        topLevelFunction("referencedClassDeclarations", visibility = Private)
                        topLevelFunction("reportPublicAccessorExposesInternal", visibility = Private)
                    }
                }
            }
            dir("core/error") {
                file("CreamException.kt") {
                    assertContent {
                        topLevelClass("CreamException")
                        topLevelClass("InvalidCreamUsageException")
                        topLevelClass("InvalidCreamOptionException")
                        topLevelClass("UnknownCreamException")
                        topLevelFunction("reportToGithub")
                    }
                }
            }

            // --- options: KSP オプションのモデルとパース（全層から参照される横断モデル） ---
            dir("options") {
                file("CopyFunNamingStrategy.kt") {
                    assertContent {
                        topLevelEnumClass("CopyFunNamingStrategy")
                        topLevelInterface("ClassDeclarationInfo")
                    }
                }
                file("CreamOptions.kt") {
                    assertContent {
                        topLevelClass("CreamOptions")
                        topLevelFunction("toCreamOptions")
                        topLevelFunction("invalidCopyFunNamingStrategyError", visibility = Private)
                        topLevelFunction("invalidEscapeDotError", visibility = Private)
                        topLevelFunction("invalidDefaultVisibilityError", visibility = Private)
                    }
                }
                file("EscapeDot.kt") {
                    assertContent { topLevelEnumClass("EscapeDot") }
                }
            }

            // --- util 層: 汎用ヘルパのみ（cream 固有型を含まない） ---
            dir("util") {
                file("EscapeIdentifier.kt") {
                    assertContent {
                        topLevelProperty("kotlinHardKeywords", visibility = Private)
                        topLevelProperty("bareIdentifierRegex", visibility = Private)
                        topLevelFunction("escapeKotlinIdentifier")
                    }
                }
                file("Sequence.kt") {
                    assertContent {
                        topLevelFunction("isCountMoreThan")
                        topLevelFunction("isCountLessThan")
                    }
                }
                file("String.kt") {
                    assertContent {
                        topLevelFunction("lines")
                        topLevelFunction("appendLines")
                    }
                }
                file("With.kt") {
                    assertContent { topLevelFunction("with") }
                }
                dir("ksp") {
                    file("KSAnnotationArgument.kt") {
                        // Boolean / String / enum / class リストの型別オーバーロード 4 つ
                        assertContent { topLevelFunction("getArgument", count = 4) }
                    }
                    file("KSClassDeclarationExt.kt") {
                        assertContent { topLevelFunction("isSealed") }
                    }
                    file("SealedHierarchy.kt") {
                        assertContent {
                            topLevelFunction("collectConcreteSubclasses")
                            topLevelFunction("collectIntermediateSealedSubclasses")
                            topLevelFunction("collectSealedAncestors")
                        }
                    }
                    file("Type.kt") {
                        assertContent { topLevelFunction("asString") }
                    }
                }
            }
        }
    }

private fun String.upperCamel(): String = replaceFirstChar { it.uppercaseChar() }

/**
 * feature の entry point ファイルの形: `internal fun processXxx` なトップレベル関数を公開し、
 * それ以外は private の補助宣言のみ許可（`privateTopLevels()` は明示的な緩和）。
 *
 * TODO(FUTURE-process-dsl.md): processXxx を DSL 化する構想があり、実現すると 10 ファイル
 *  すべての形が一斉に変わるため、意図的に宣言の個数・シグネチャ（戻り値 / context parameter）
 *  までは固定していない。DSL 化が済んだら「実態に厳密」（個数まで固定）に締めること。
 *  それまでのシグネチャ検査（List<KSAnnotated> / context(ProcessContext)）は
 *  cream-ksp 側の feature/ArchTest が担っている。
 */
private fun processFeatureFileContent(): FileContentManifest =
    fileContentManifest {
        topLevelFunctions(nameStartsWith = "process", visibility = Internal, required = true)
        // private の補助宣言（annotationName / 診断ヘルパ / 集計用クラス）は DSL 化まで個数を固定しない
        privateTopLevels()
    }
