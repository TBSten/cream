package me.tbsten.cream.test.architecture.tool.aiAgent

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.mcpConfigRole() =
    "McpConfig".role {
        documented = false
        title = "MCP サーバの設定"
        summary = "AI エージェントが使う MCP サーバの設定"
        example(".mcp.json", "MCP サーバの設定")

        layout {
            ".mcp.json".file()
        }
    }
