package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.ImportsBuilder
import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Internal
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Private

private const val KSP_CORE_PACKAGE = "${ManifestConstants.KSP_BASE_PACKAGE}.core"
private const val KSP_API_PACKAGE = ManifestConstants.KSP_API_PACKAGE

private val copyFunImports: ImportsBuilder.() -> Unit = {
    packageTree(KSP_API_PACKAGE)
    packageTree("$KSP_CORE_PACKAGE.common")
    packageTree(KSP_OPTIONS_FULL_PACKAGE)
    packageTree(KSP_UTIL_PACKAGE)
}

private val combineFunImports: ImportsBuilder.() -> Unit = {
    packageTree(KSP_API_PACKAGE)
    packageTree("$KSP_CORE_PACKAGE.common")
    packageTree("$KSP_CORE_PACKAGE.error")
    packageTree(KSP_OPTIONS_FULL_PACKAGE)
    packageTree(KSP_UTIL_PACKAGE)
}

private val commonImports: ImportsBuilder.() -> Unit = {
    packageTree("kotlin") // kotlin.reflect.KClass など標準ライブラリ
    packageTree(KSP_API_PACKAGE)
    packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE) // 注釈・token const
    packageTree("$KSP_CORE_PACKAGE.common") // 同 zone 内の別ファイル参照（alias import 含む）
    packageTree("$KSP_CORE_PACKAGE.error")
    packageTree(KSP_OPTIONS_FULL_PACKAGE)
    packageTree(KSP_UTIL_PACKAGE)
}

private val sealedCopyImports: ImportsBuilder.() -> Unit = {
    packageTree(KSP_API_PACKAGE)
    packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE)
    packageTree("$KSP_CORE_PACKAGE.common")
    packageTree("$KSP_CORE_PACKAGE.error")
    packageTree(KSP_OPTIONS_FULL_PACKAGE)
    packageTree(KSP_UTIL_PACKAGE)
}

private val parentOptionalImports: ImportsBuilder.() -> Unit = {
    packageTree(KSP_API_PACKAGE)
    packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE)
    packageTree("$KSP_CORE_PACKAGE.common")
    packageTree("$KSP_CORE_PACKAGE.error")
    packageTree(KSP_OPTIONS_FULL_PACKAGE)
    packageTree(KSP_UTIL_PACKAGE)
}

internal fun ManifestDirBuilder.core() {
    dir("core") {
        common()
        error()

        appendFunctions(
            path = "copyFun",
            entryPointFile = "Transform.kt",
            entryPointFunction = "appendCopyFunction",
            zoneImports = copyFunImports,
        )
        appendFunctions(
            path = "combineFun",
            entryPointFile = "Transform.kt",
            entryPointFunction = "appendCombineToFunction",
            zoneImports = combineFunImports,
        ) {
            // KDoc の例文組み立てだけは「Appendable.append*」規約の外（KDocAppender 拡張 + 文字列組み立て）。
            ktFile("CombineExample.kt") {
                imports(combineFunImports)
                topLevelFunction("appendCombineAutoDescription", receiver = "KDocAppender")
                topLevelFunction("combineExampleBody", returns = "String")
                topLevels(Private)
            }
        }

        // TODO remove by refactor
        sealedCopy()
        parentOptional()
    }
}

private fun ManifestDirBuilder.common() {
    dir("common") {
        ktFile("CodeGeneratorExt.kt") {
            imports(commonImports)
            topLevelFunction("createNewKotlinFile")
        }
        ktFile("CombineSourceAnnotation.kt") {
            imports(commonImports)
            topLevelClass("CombineToSourceAnnotation")
            topLevelClass("CombineFromSourceAnnotation")
        }
        ktFile("CopyFunctionBodyParts.kt") {
            imports(commonImports)
            topLevelFunction("appendTypeParameterList")
            topLevelFunction("appendSourceClassWithTypeArgs")
            topLevelFunction("appendTargetClassWithTypeArgs")
            topLevelFunction("resolveConstructorParamTypeParameter")
            topLevelFunction("appendWhereClause")
            topLevelFunction("appendConstructorCallBody")
        }
        ktFile("CopyFunctionName.kt") {
            imports(commonImports)
            topLevelFunction("copyFunctionName")
            topLevelClass("CopyFunctionName")
        }
        ktFile("CopyFunctionNameExt.kt") {
            imports(commonImports)
            topLevelFunction("toClassDeclarationInfo")
            topLevelFunction("copyFunctionName")
            topLevelFunction("resolveFunName")
            topLevelFunction("resolveSealedCopyFunName")
        }
        ktFile("CopyFunctionTypeParameters.kt") {
            imports(commonImports)
            topLevelFunction("getCopyFunctionTypeParameters")
            topLevelClass("CopyFunctionTypeParameters")
            topLevelFunction("copyFunctionTypeParametersMap", visibility = Private)
            topLevelClass("CopyFunctionTypeParameter")
        }
        ktFile("CopySourceAnnotation.kt") {
            imports(commonImports)
            topLevelClass("CopyToSourceAnnotation")
            topLevelClass("CopyFromSourceAnnotation")
            topLevelClass("CopyToChildrenSourceAnnotation")
            topLevelClass("SealedCopySourceAnnotation")
        }
        ktFile("DeclarationName.kt") {
            imports(commonImports)
            // KSDeclaration / KClass<*> の 2 レシーバ分
            topLevelProperty("fullName", count = 2)
            topLevelProperty("underPackageName")
        }
        ktFile("DeprecatedPropagation.kt") {
            imports(commonImports)
            topLevelFunction("deprecatedAnnotationLine")
            topLevelFunction("toAnnotationLine")
            topLevelFunction("deprecatedAnnotationOrNull")
            topLevelFunction("firstDeprecation", visibility = Private)
            topLevelFunction("toKotlinStringLiteral", visibility = Private)
        }
        ktFile("ExcludeProperty.kt") {
            imports(commonImports)
            topLevelFunction("isExcludedFromCopy")
            topLevelFunction("isSourcePropertyExcluded")
            topLevelFunction("warnIfTargetExcludeHasNoEffect")
            topLevelFunction("warnIfSourceExcludeHasNoEffect")
            topLevelClass("MappingExcludesDirection")
            topLevelFunction("warnIfMappingExcludesHaveNoEffect")
            topLevelFunction("concreteTargets", visibility = Private)
        }
        ktFile("FindMappedSourceProperty.kt") {
            imports(commonImports)
            topLevelTypeAlias("FindMappedSourceProperty")
        }
        ktFile("FindMatchedProperty.kt") {
            // プロパティ照合（@Map / 名前一致 / デフォルト値）の分岐が密結合で、無理に割ると追いにくくなるため上限 500
            maxLines(500)
            imports(commonImports)
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
        ktFile("FunName.kt") {
            imports(commonImports)
            topLevelFunction("funNameTemplate")
        }
        ktFile("FunNameValidation.kt") {
            imports(commonImports)
            topLevelFunction("invalidFunNameException", visibility = Private)
            topLevelEnumClass("FunNameValidity")
            topLevelProperty("isValid")
            topLevelFunction("onInvalid")
            topLevelFunction("validateFunName")
        }
        ktFile("FunctionNameTemplate.kt") {
            imports(commonImports)
            topLevelFunction("resolveFunNameTemplate")
            topLevelFunction("containsAnyCopyFunNameToken")
            topLevelFunction("pascalCase", visibility = Private)
            topLevelFunction("snakeCase", visibility = Private)
            topLevelProperty("copyTargetTokenExpanders", visibility = Private)
        }
        ktFile("GenerateSourceAnnotation.kt") {
            imports(commonImports)
            topLevelInterface("GenerateSourceAnnotation")
        }
        ktFile("IsExcluded.kt") {
            imports(commonImports)
            topLevelTypeAlias("IsExcluded")
        }
        ktFile("KDoc.kt") {
            imports(commonImports)
            topLevelFunction("appendAutoGeneratedFunctionKDoc")
            topLevelClass("KDocAppender")
            topLevelFunction("appendExample")
            topLevelFunction("appendUserKDocBlock", visibility = Private)
            topLevelFunction("autoGenerateKDoc", visibility = Private)
        }
        ktFile("KSAnnotationExt.kt") {
            imports(commonImports)
            topLevelFunction("annotationsOf")
            topLevelFunction("classListArgument")
            topLevelFunction("resolveClassListOrReport")
            topLevelFunction("extractPropertyMappings")
            topLevelFunction("extractExcludes")
            topLevelFunction("extractKDoc")
        }
        ktFile("MappingSourceAnnotation.kt") {
            imports(commonImports)
            topLevelClass("CopyMappingSourceAnnotation")
            topLevelClass("CombineMappingSourceAnnotation")
        }
        ktFile("NotCopyToObject.kt") {
            imports(commonImports)
            topLevelFunction("notCopyToObject")
        }
        ktFile("OmitPackages.kt") {
            imports(commonImports)
            topLevelFunction("omitPackagesFor")
        }
        ktFile("ParentOptionalSourceAnnotation.kt") {
            imports(commonImports)
            topLevelClass("ParentOptionalSourceAnnotation")
            topLevelClass("ChildOptionalsSourceAnnotation")
            topLevelFunction("parentOptionalPropertyNameOrNull", visibility = Private)
            topLevelFunction("resolveAccessorName", visibility = Private)
        }
        ktFile("ReportCreamError.kt") {
            imports(commonImports)
            topLevelFunction("reportCreamError")
            topLevelFunction("asDeclarationOrReport")
            topLevelFunction("asClassDeclarationOrReport")
        }
        ktFile("SealedTypeRendering.kt") {
            imports(commonImports)
            topLevelFunction("renderWhenBranchType")
            topLevelFunction("starProjectedTypeText", visibility = Private)
            topLevelFunction("renderTypeParameterList")
            topLevelFunction("renderWhereClause")
            topLevelFunction("renderSealedReceiverType")
        }
        ktFile("TargetValidation.kt") {
            imports(commonImports)
            topLevelEnumClass("CopyTargetRejection")
            topLevelFunction("concreteClassRejection")
            topLevelFunction("constructorRejection", visibility = Private)
        }
        ktFile("TypeAlias.kt") {
            imports(commonImports)
            topLevelFunction("resolveToClassDeclaration")
            topLevelFunction("unresolvableClassException", visibility = Private)
            topLevelFunction("resolveClassDeclarationOrReport")
        }
        ktFile("ValueClassConversionProbe.kt") {
            imports(commonImports)
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
        ktFile("ValueClassMapping.kt") {
            imports(commonImports)
            topLevelInterface("ValueClassConversion")
            topLevelInterface("ValueClassConversionOutcome")
            topLevelFunction("defaultValueExpression")
            topLevelFunction("findValueClassConversion")
            topLevelFunction("findValueClassConversionOutcome")
        }
        ktFile("Visibility.kt") {
            imports(commonImports)
            topLevelProperty("visibilityStr")
            topLevelFunction("toModifierString")
            topLevelFunction("copyVisibilityArgument")
        }
    }
}

private fun ManifestDirBuilder.error() {
    dir("error") {
        ktFile("CreamException.kt") {
            imports {
                packageTree(KSP_UTIL_PACKAGE)
            }
            topLevelClass("CreamException")
            // 例外階層はこのファイルに集約する: CreamException を（間接も含めて）継承した
            // *Exception だけを許可（無関係な例外クラスの持ち込みは deny）。
            topLevelClasses(nameEndsWith = "Exception", visibilities = setOf(Internal), extends = "CreamException")
            topLevelFunction("reportToGithub", returns = "String")
        }
    }
}

private fun ManifestDirBuilder.appendFunctions(
    path: String,
    entryPointFile: String,
    entryPointFunction: String,
    zoneImports: ImportsBuilder.() -> Unit,
    block: ManifestDirBuilder.() -> Unit = {},
) {
    dir(path) {
        ktFile(entryPointFile, required = true) {
            imports(zoneImports)
            topLevelFunction(
                entryPointFunction,
                receiver = "Appendable",
                contextParameters = setOf("CreamOptions", "KSPLogger"),
                returns = "Unit",
            )
            topLevels(Private)
        }

        block()

        ktFile("*.kt") {
            imports(zoneImports)
            topLevelFunctions(nameStartsWith = "append", visibilities = setOf(Internal), receiver = "Appendable")
            topLevels(Private)
        }
    }
}

private fun ManifestDirBuilder.sealedCopy() {
    dir("sealedCopy") {
        ktFile("SealedCopy.kt") {
            imports(sealedCopyImports)
            topLevelFunction("appendSealedCopyFunction")
            topLevelFunction("appendSealedCopyHeader", visibility = Private)
            topLevelFunction("appendSealedCopyBody", visibility = Private)
        }
        ktFile("SealedCopyAssignability.kt") {
            imports(sealedCopyImports)
            topLevelFunction("isParameterAssignableFromProperty")
        }
        ktFile("SealedCopyDiagnostic.kt") {
            imports(sealedCopyImports)
            topLevelFunction("nonCopyableErrorException")
        }
        ktFile("SealedCopyKDoc.kt") {
            imports(sealedCopyImports)
            topLevelFunction("appendSealedCopyKDoc")
        }
        ktFile("SealedCopyLeaf.kt") {
            imports(sealedCopyImports)
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
        ktFile("SealedCopyViaValidation.kt") {
            imports(sealedCopyImports)
            topLevelClass("SealedCopyViaError")
            topLevelFunction("collectSealedCopyViaErrors")
        }
    }
}

private fun ManifestDirBuilder.parentOptional() {
    dir("parentOptional") {
        ktFile("ParentOptionalAccessor.kt") {
            imports(parentOptionalImports)
            topLevelFunction("appendParentOptionalAccessor")
            topLevelFunction("appendParentOptionalKDoc", visibility = Private)
            topLevelFunction("deprecatedAnnotationLineOrNull", visibility = Private)
        }
        ktFile("ParentOptionalAnnotationLookup.kt") {
            imports(parentOptionalImports)
            topLevelFunction("parentOptionalAnnotationOrNull")
            topLevelFunction("correspondingConstructorParameter")
            // KSPropertyDeclaration / KSClassDeclaration の 2 レシーバ分
            topLevelFunction("isAccessibleFromGeneratedAccessor", count = 2)
            topLevelFunction("isExtensionProperty")
            topLevelFunction("reportParentOptionalExtensionProperty")
            topLevelFunction("firstInaccessibleClassOrNull")
            topLevelFunction("classChain")
        }
        ktFile("ParentOptionalEntryOrder.kt") {
            imports(parentOptionalImports)
            topLevelFunction("withEntriesMostDerivedFirst")
        }
        ktFile("ParentOptionalTarget.kt") {
            imports(parentOptionalImports)
            topLevelClass("ParentOptionalEntry")
            topLevelClass("ParentOptionalAccessorSpec")
            topLevelFunction("validatedPropertyTypeTextOrNull")
            topLevelProperty("isPropertyTypeNullable")
            topLevelFunction("renderPropertyTypeOrNull", visibility = Private)
            topLevelFunction("childTypeParamToParentName", visibility = Private)
            topLevelFunction("typeParameterNamesUnpinnedBy")
        }
        ktFile("ParentOptionalVisibility.kt") {
            imports(parentOptionalImports)
            topLevelFunction("validatedVisibilityModifierOrNull")
            topLevelFunction("inheritedNarrowestModifier", visibility = Private)
            topLevelFunction("firstExposedInternalDeclarationOrNull", visibility = Private)
            topLevelFunction("referencedClassDeclarations", visibility = Private)
            topLevelFunction("reportPublicAccessorExposesInternal", visibility = Private)
        }
    }
}
