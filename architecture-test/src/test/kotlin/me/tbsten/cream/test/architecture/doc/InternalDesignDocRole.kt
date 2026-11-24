package me.tbsten.cream.test.architecture.doc

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.internalDesignDocRole() =
    "InternalDesignDoc".role {
        title = "内部設計メモ"
        summary = "開発者向けの設計メモ（利用者向けドキュメントには含めない）"
        example("doc/internal/feature-and-options.md", "機能とオプションの対応表")

        layout {
            "doc" / "internal" /
                "**" {
                    "*.md".file()
                    "*.html".file()
                }
        }
    }
