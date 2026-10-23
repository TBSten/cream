package me.tbsten.cream.test.architecture.tools.claudeCode.commands

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.commands() =
    "Commands" {
        summary = "A Claude Code slash command"
        example("implements.md")
        layout {
            ".claude/commands" / "*.md".file()
        }
    }
