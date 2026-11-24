package me.tbsten.cream.test.architecture.tool.aiAgent

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.context7ConfigRole() =
    "Context7Config".role {
        documented = false
        title = "Context7 の設定"
        summary = "Context7 にライブラリのドキュメントを登録する設定"
        example("context7.json", "Context7 の設定")

        layout {
            "context7.json".file()
        }
    }
