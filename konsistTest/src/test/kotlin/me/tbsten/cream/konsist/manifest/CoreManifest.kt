package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.KtFileBuilder
import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Internal
import me.tbsten.cream.konsist.dsl.TopLevelVisibility.Private
import me.tbsten.cream.konsist.dsl.importCreamKsp
import me.tbsten.cream.konsist.dsl.importCreamRuntime
import me.tbsten.cream.konsist.dsl.importKotlinLibrary
import me.tbsten.cream.konsist.dsl.importKspApi

/**
 * core の規約は「feature（と root）に依存しない」ことだけで、直下パッケージ
 * （common / copyFun / combineFun / sealedCopy / parentOptional / error）の区別はしない。
 * どの直下パッケージでも [coreFileContent] の条件を満たせばよい。
 */
internal fun ManifestDirBuilder.core() {
    dir("core") {
        // `*` は `/` を跨がないので「core の直下パッケージにある .kt」だけが対象。
        // core 直下へのファイル置きと 3 階層目は配置違反になる。
        ktFile("*/*.kt") { coreFileContent() }

        // 唯一の行数例外: プロパティ照合（@Map / 名前一致 / デフォルト値）の分岐が密結合で、
        // 無理に割ると追いにくくなるため上限 500。
        ktFile("common/FindMatchedProperty.kt") {
            maxLines(500)
            coreFileContent()
        }
    }
}

private fun KtFileBuilder.coreFileContent() {
    imports {
        importKotlinLibrary()
        importKspApi()
        importCreamRuntime()
        importCreamKsp.core()
        importCreamKsp.options()
        importCreamKsp.util()
    }
    topLevels(Internal, Private)
}
