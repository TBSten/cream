package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Internal
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Public
import me.tbsten.cream.konsist.dsl.importCreamKsp
import me.tbsten.cream.konsist.dsl.importKspApi

internal fun ManifestDirBuilder.kspEntryPoint() {
    ktFile("CreamSymbolProcessor.kt", required = true) {
        imports {
            importKspApi()
            importCreamKsp.core()
            importCreamKsp.feature()
            importCreamKsp.options()
        }
        topLevelClass("CreamSymbolProcessor", visibility = Internal)
    }

    ktFile("CreamSymbolProcessorProvider.kt", required = true) {
        imports {
            importKspApi()
        }
        // cream-ksp/main で唯一の public（KSP のエントリポイント）
        topLevelClass("CreamSymbolProcessorProvider", visibility = Public)
    }

    ktFile("ProcessContext.kt", required = true) {
        imports {
            importKspApi()
            importCreamKsp.options()
        }
        topLevelClass("ProcessContext", visibility = Internal)
    }
}
