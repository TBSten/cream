package me.tbsten.cream.test.architecture.tool.aiAgent.claude

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.claudeRuleRole() =
    "ClaudeRule".role {
        documented = false
        title = "Claude Code のルール"
        summary = "テーマごとに分けた Claude Code 向けのルール"
        example(".claude/rules/ksp-architecture.md", "cream-ksp の層構成のルール")

        layout {
            ".claude" / "rules" / "*.md".file()
        }
    }
