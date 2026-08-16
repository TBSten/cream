package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Internal
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Private

internal const val KSP_OPTIONS_PACKAGE_NAME = "options"
internal const val KSP_OPTIONS_FULL_PACKAGE = "${ManifestConstants.KSP_BASE_PACKAGE}.$KSP_OPTIONS_PACKAGE_NAME"

internal fun ManifestDirBuilder.options() {
    dir(KSP_OPTIONS_PACKAGE_NAME) {
        ktFile("CreamOptions.kt") {
            imports {
                packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE) // CopyVisibility
                packageTree(KSP_UTIL_PACKAGE) // util.lines
                // パース失敗の送出。options → core/error は依存方向テーブルで正当な唯一の core 依存。
                packageEquals("${ManifestConstants.KSP_BASE_PACKAGE}.core.error")
                packageEquals("kotlin.reflect") // KProperty1
            }
            topLevelClass("CreamOptions")
            topLevelFunction("toCreamOptions")
            // invalidCopyFunNamingStrategyError などのパースエラー診断ヘルパ。
            topLevels(Private)
        }

        // enum だけの option 定義ファイル（CopyFunNamingStrategy.kt / EscapeDot.kt）。
        ktFile("*.kt") {
            imports {
                packageEquals(ManifestConstants.RUNTIME_BASE_PACKAGE)
                packageTree(KSP_UTIL_PACKAGE)
            }
            topLevels(Internal)
        }
    }
}
