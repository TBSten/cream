package me.tbsten.cream.test.architecture.tools.claudeCode.rules

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.rules() =
    "Rules" {
        summary = "A path-scoped rule (`paths:` front matter) under `.claude/rules`"
        example("ksp-architecture.md")
        layout {
            ".claude/rules" / "*.md".file()
        }
    }
