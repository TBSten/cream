package me.tbsten.cream.test.architecture.tools.claudeCode.agentInstructions

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.agentInstructions() =
    "AgentInstructions" {
        summary = "A CLAUDE.md file loaded into every session"
        example("CLAUDE.md")
        layout {
            "**/CLAUDE.md".file()
        }
    }
