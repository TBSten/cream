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
            ktFile("$feature/Process${feature.upperCamel()}.kt", required = true) { processFeatureFileContent() }
            ktFile("$feature/Diagnostics.kt") { diagnosticsFeatureFileContent() }
        }
    }
}

private fun String.upperCamel(): String = replaceFirstChar { it.uppercaseChar() }

private fun KtFileBuilder.processFeatureFileContent() {
    imports(featureImports)
    topLevelFunctions(
        nameStartsWith = "process",
        visibilities = setOf(Internal),
        required = true,
    )
    // private の補助宣言（annotationName / 診断ヘルパ / 集計用クラス）は DSL 化まで個数を固定しない
    topLevels(Private)
}

private fun KtFileBuilder.diagnosticsFeatureFileContent() {
    imports(featureImports)
    topLevelFunctions(
        nameStartsWith = "warn",
        visibilities = setOf(Internal, Private),
    )
    topLevelFunctions(
        nameStartsWith = "report",
        visibilities = setOf(Internal, Private),
    )
    // annotationName など、診断メッセージ組み立て用の private 補助宣言
    topLevels(Private)
}
