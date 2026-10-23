package me.tbsten.cream.test.architecture.tools.claudeCode.agents

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.agents() =
    "Agents" {
        summary = "A Claude Code subagent definition"
        example("pr-creator.md")
        layout {
            ".claude/agents" / "*.md".file()
        }
    }
