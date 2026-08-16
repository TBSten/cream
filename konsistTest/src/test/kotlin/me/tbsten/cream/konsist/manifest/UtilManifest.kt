package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.KtFileBuilder
import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility

internal const val KSP_UTIL_DIR_NAME = "util"
internal const val KSP_UTIL_PACKAGE = "${ManifestConstants.KSP_BASE_PACKAGE}.$KSP_UTIL_DIR_NAME"

internal fun ManifestDirBuilder.util() {
    dir(KSP_UTIL_DIR_NAME) {
        dir("ksp") {
            ktFile("*.kt") { utilKspFileContent() }
        }
        // util 直下: KSP にも依存しない汎用ヘルパ。
        ktFile("*.kt") { utilFileContent() }
    }
}

private fun KtFileBuilder.utilFileContent() {
    imports {
        packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE)
        packageTree(KSP_UTIL_PACKAGE)
    }
    // private: EscapeIdentifier.kt の keyword 表・regex などの補助宣言。
    topLevels(visibilities = setOf(TopLevelVisibility.Internal, TopLevelVisibility.Private))
}

private fun KtFileBuilder.utilKspFileContent() {
    imports {
        packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE)
        packageTree(KSP_UTIL_PACKAGE)
        // kotlin.reflect.KClass / KProperty（KSAnnotationArgument.kt）。
        packageEquals("kotlin.reflect")
        packageTree("com.google.devtools.ksp")
    }
    topLevels(visibilities = setOf(TopLevelVisibility.Internal, TopLevelVisibility.Private))
}
