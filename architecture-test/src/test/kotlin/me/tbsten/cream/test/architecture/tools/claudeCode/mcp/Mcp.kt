package me.tbsten.cream.test.architecture.tools.claudeCode.mcp

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.mcp() =
    "Mcp" {
        summary = "The MCP servers Claude Code starts for this repository (CodeGraph)"
        example(".mcp.json")
        layout {
            ".mcp.json".file()
        }
    }
