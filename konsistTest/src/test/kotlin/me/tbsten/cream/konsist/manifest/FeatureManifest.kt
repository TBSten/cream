package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.ImportsBuilder
import me.tbsten.cream.konsist.dsl.KtFileBuilder
import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Internal
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Private

private val FEATURES =
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

private val featureImports: ImportsBuilder.() -> Unit = {
    packageTree(ManifestConstants.KSP_API_PACKAGE)
    packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE) // 注釈（@CopyTo 等）と NonCopyableStrategy
    packageTree("${ManifestConstants.KSP_BASE_PACKAGE}.core")
    packageTree(KSP_OPTIONS_FULL_PACKAGE)
    packageTree(KSP_UTIL_PACKAGE)
    // feature が上向きに依存してよい唯一の root 型。packageEquals(ksp 直下) にすると
    // CreamSymbolProcessor / Provider まで開いてしまうので 1 型だけ許可する
    fqName("${ManifestConstants.KSP_BASE_PACKAGE}.ProcessContext")
}

internal fun ManifestDirBuilder.feature() {
    dir("feature") {
        FEATURES.forEach { feature ->
            requiredKtFile("$feature/Process${feature.upperCamel()}.kt") { processFeatureFileContent() }
        }
        // 診断ヘルパの分割ファイル。実在するのは childOptionals のみで、実態に厳密に列挙する。
        // 旧 manifest は他 feature にも同名パターンで事前許可していたが、中身の grant を
        // 書けない架空ファイルの事前許可は deny by default と矛盾するため廃止した。
        // TODO: 他 feature に Diagnostics 分割ファイルが必要になったら、人間がその実態を列挙して足すこと。
        ktFile("childOptionals/ChildOptionalsDiagnostics.kt") {
            imports(featureImports)
            topLevelProperty("annotationName", visibility = Private)
            topLevelFunction("warnChildOptionalsUnpinnedTypeParameters")
            topLevelFunction("warnChildOptionalsExcludeHasNoEffect")
            topLevelFunction("reportChildOptionalsNotADeclaration")
            topLevelFunction("reportChildOptionalsNotSealed")
        }
    }
}

private fun String.upperCamel(): String = replaceFirstChar { it.uppercaseChar() }

private fun KtFileBuilder.processFeatureFileContent() {
    imports(featureImports)
    topLevelFunctions(
        nameStartsWith = "process",
        visibility = Internal,
        required = true,
    )
    // private の補助宣言（annotationName / 診断ヘルパ / 集計用クラス）は DSL 化まで個数を固定しない
    topLevels(Private)
}
