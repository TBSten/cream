package me.tbsten.cream.konsist.dsl

import me.tbsten.cream.konsist.manifest.KSP_OPTIONS_FULL_PACKAGE
import me.tbsten.cream.konsist.manifest.KSP_UTIL_PACKAGE
import me.tbsten.cream.konsist.manifest.ManifestConstants

/**
 * manifest 側で何度も出てくる import 許可の語彙。許可の意味（どこまで開くか）を 1 箇所に集め、
 * データ側は「何を開くか」だけを書けばよいようにする。
 *
 * `packageTree`（サブパッケージ含む）と `packageEquals`（直下メンバーのみ）のどちらを使うかは
 * ここで固定する — 取り違えると deny by default が黙って広がるため。
 *
 * @see manifest DSL 意味論の大筋
 */
internal fun ImportsBuilder.importKotlinLibrary() {
    packageTree("kotlin")
}

internal fun ImportsBuilder.importKotlinReflect() {
    packageEquals("kotlin.reflect")
}

internal fun ImportsBuilder.importKspApi() {
    packageTree(ManifestConstants.KSP_API_PACKAGE)
}

/** runtime は直下メンバー（注釈・token const）だけ。packageTree にすると ksp 配下まで開いてしまう。 */
internal fun ImportsBuilder.importCreamRuntime() {
    packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE)
}

internal val ImportsBuilder.importCreamKsp get() = ImportCreamKsp(importsBuilder = this)

internal class ImportCreamKsp(
    private val importsBuilder: ImportsBuilder,
) {
    fun util() =
        with(importsBuilder) {
            packageTree(KSP_UTIL_PACKAGE)
        }

    fun options() =
        with(importsBuilder) {
            packageTree(KSP_OPTIONS_FULL_PACKAGE)
        }

    fun core() =
        with(importsBuilder) {
            packageTree("${ManifestConstants.KSP_BASE_PACKAGE}.core")
        }

    fun coreError() =
        with(importsBuilder) {
            packageEquals("${ManifestConstants.KSP_BASE_PACKAGE}.core.error")
        }

    fun feature() =
        with(importsBuilder) {
            packageTree("${ManifestConstants.KSP_BASE_PACKAGE}.feature")
        }

    /** feature が上向きに依存してよい唯一の root 型。ksp 直下を開くと Processor / Provider まで通ってしまう。 */
    fun processContext() =
        with(importsBuilder) {
            fqName("${ManifestConstants.KSP_BASE_PACKAGE}.ProcessContext")
        }
}
