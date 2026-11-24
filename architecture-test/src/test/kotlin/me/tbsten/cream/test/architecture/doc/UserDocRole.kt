@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.doc

import me.tbsten.cream.test.architecture.support.`対になるファイルがある`
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.FileConstraintScope

fun DeclarationContainerScope.userDocRole() =
    "UserDoc".role {
        title = "利用者向けドキュメント"
        summary = "利用者向けの機能説明・カスタマイズ・ユースケース（英語版と日本語版 *.ja.md の対）と、その画像"
        example("doc/copy.md", "copy 関数の説明")
        example("doc/customization/options.ja.md", "オプションの説明（日本語）")

        layout {
            "doc" {
                "*.md".file()
                "*.png".file()
                "customization" / "*.md".file()
                "use-case" / "**" / "*.md".file()
                `英語版と日本語版が対になっている`()
            }
        }
    }

private fun FileConstraintScope.`英語版と日本語版が対になっている`() =
    `対になるファイルがある`(
        name = "英語版と日本語版が対になっている",
        howToFix = "対になる英語版（*.md）か日本語版（*.ja.md）を追加する",
    ) { file ->
        when {
            file.endsWith(".ja.md") -> file.removeSuffix(".ja.md") + ".md"
            file.endsWith(".md") -> file.removeSuffix(".md") + ".ja.md"
            else -> null
        }
    }
