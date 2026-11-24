package me.tbsten.cream.test.architecture.tool

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.editorConfigRole() =
    "EditorConfig".role {
        documented = false
        title = "EditorConfig"
        summary = "エディタと ktlint の書式設定"
        example(".editorconfig", "ルートの設定")

        layout {
            ".editorconfig".file()
        }
    }
