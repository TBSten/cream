package me.tbsten.cream.test.architecture.tool.aiAgent.claude

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.claudeSkillRole() =
    "ClaudeSkill".role {
        documented = false
        title = "Claude Code の skill"
        summary = "このリポジトリで使う Claude Code の skill（.claude/skills）"
        description =
            """
            skill ごとに 1 ディレクトリ。中身の構成は各 skill が決める。
            """.trimIndent()
        example(".claude/skills/release-note/SKILL.md", "リリースノート作成 skill")
        example(".claude/skills/katachi/references/setup/README.md", "skill から読む手順書")

        layout {
            ".claude" / "skills" / "*/**/*".file()
        }
    }
