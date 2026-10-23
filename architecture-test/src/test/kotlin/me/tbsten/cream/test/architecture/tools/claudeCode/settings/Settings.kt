package me.tbsten.cream.test.architecture.tools.claudeCode.settings

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.settings() =
    "Settings" {
        summary = "The Claude Code project settings"
        example("settings.json")
        layout {
            ".claude/settings.json".file()
        }
    }
