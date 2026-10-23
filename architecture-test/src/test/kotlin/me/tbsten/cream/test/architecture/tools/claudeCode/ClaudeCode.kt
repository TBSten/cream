package me.tbsten.cream.test.architecture.tools.claudeCode

import me.tbsten.cream.test.architecture.tools.claudeCode.agentInstructions.agentInstructions
import me.tbsten.cream.test.architecture.tools.claudeCode.agents.agents
import me.tbsten.cream.test.architecture.tools.claudeCode.commands.commands
import me.tbsten.cream.test.architecture.tools.claudeCode.mcp.mcp
import me.tbsten.cream.test.architecture.tools.claudeCode.rules.rules
import me.tbsten.cream.test.architecture.tools.claudeCode.settings.settings
import me.tbsten.cream.test.architecture.tools.claudeCode.skills.skills
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.claudeCode() =
    "claudeCode".group {
        title = "Claude Code"
        summary = "Instructions and extensions Claude Code reads in this repository"

        agentInstructions()
        rules()
        skills()
        agents()
        commands()
        settings()
        mcp()
    }
