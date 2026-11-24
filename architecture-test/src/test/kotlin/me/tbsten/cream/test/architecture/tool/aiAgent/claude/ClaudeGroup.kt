package me.tbsten.cream.test.architecture.tool.aiAgent.claude

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.claudeGroup() =
    "Claude".group {
        documented = false
        summary = "Claude Code が読む指示・ルール・skill・設定"

        claudeMdRole()
        claudeRuleRole()
        claudeSkillRole()
        claudeSettingsRole()
    }
