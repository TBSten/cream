package me.tbsten.cream.test.architecture.tool

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.gitIgnoreRole() =
    "GitIgnore".role {
        documented = false
        title = "Git ignore"
        summary = "Git の追跡から外すファイルの一覧（.gitignore）"
        example(".gitignore", "ルートの .gitignore")

        layout {
            "**/.gitignore".file()
        }
    }
