package me.tbsten.cream.test.architecture.tools.gitIgnore

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.gitIgnore() =
    "GitIgnore" {
        summary = "A list of files kept out of Git (.gitignore)"
        example(".gitignore")
        layout {
            "**/.gitignore".file()
        }
    }
