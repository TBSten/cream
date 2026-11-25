package me.tbsten.cream.test.architecture.tool.aiAgent

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.agentSkillRole() =
    "AgentSkill".role {
        documented = false
        title = "エージェントの skill"
        summary = "Claude Code 以外の AI エージェント向けに、このリポジトリで使う skill（.agents/skills）"
        description =
            """
            skill ごとに 1 ディレクトリ。中身の構成は各 skill が決める。
            """.trimIndent()
        example(".agents/skills/katachi/SKILL.md", "katachi の skill")
        example(".agents/skills/katachi/references/setup/README.md", "skill から読む手順書")

        layout {
            ".agents" / "skills" / "*/**/*".file()
        }
    }
