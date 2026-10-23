package me.tbsten.cream.test.architecture.tools.editorConfig

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.editorConfig() =
    "EditorConfig" {
        summary = "The file that configures ktlint's rules (.editorconfig)"
        example(".editorconfig")
        layout {
            ".editorconfig".file()
        }
    }
