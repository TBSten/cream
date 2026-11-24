package me.tbsten.cream.test.architecture.tool.aiAgent.claude

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.claudeSettingsRole() =
    "ClaudeSettings".role {
        documented = false
        title = "Claude Code の設定"
        summary = "Claude Code の権限やフックの設定"
        example(".claude/settings.json", "Claude Code の設定")

        layout {
            ".claude" / "settings.json".file()
        }
    }
