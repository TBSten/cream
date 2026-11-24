package me.tbsten.cream.test.architecture.tool.aiAgent

import me.tbsten.cream.test.architecture.tool.aiAgent.claude.claudeGroup
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.aiAgentGroup() =
    "AiAgent".group {
        documented = false
        summary = "AI エージェント向けの指示・skill・設定"

        claudeGroup()
        agentSkillRole()
        mcpConfigRole()
        context7ConfigRole()
    }
