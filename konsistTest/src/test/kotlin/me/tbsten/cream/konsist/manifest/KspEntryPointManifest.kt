package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Internal
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Public

internal fun ManifestDirBuilder.kspEntryPoint() {
    requiredKtFile("CreamSymbolProcessor.kt") {
        imports {
            packageTree(ManifestConstants.KSP_API_PACKAGE)
            packageTree("${ManifestConstants.KSP_BASE_PACKAGE}.core")
            packageTree("${ManifestConstants.KSP_BASE_PACKAGE}.feature")
            packageTree(KSP_OPTIONS_FULL_PACKAGE)
        }
        topLevelClass("CreamSymbolProcessor", visibility = Internal)
    }
    requiredKtFile("CreamSymbolProcessorProvider.kt") {
        imports {
            packageTree(ManifestConstants.KSP_API_PACKAGE)
        }
        // cream-ksp/main で唯一の public（KSP のエントリポイント）
        topLevelClass("CreamSymbolProcessorProvider", visibility = Public)
    }
    requiredKtFile("ProcessContext.kt") {
        imports {
            packageTree(ManifestConstants.KSP_API_PACKAGE)
            packageTree(KSP_OPTIONS_FULL_PACKAGE)
        }
        topLevelClass("ProcessContext", visibility = Internal)
    }
}
