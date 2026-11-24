package me.tbsten.cream.test.architecture.tool.aiAgent.claude

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.claudeMdRole() =
    "ClaudeMd".role {
        documented = false
        title = "CLAUDE.md"
        summary = "Claude Code がいつも読む指示（置いたディレクトリ以下に効く）"
        example("CLAUDE.md", "プロジェクト全体の指示")

        layout {
            "**" / "CLAUDE.md".file()
        }
    }
