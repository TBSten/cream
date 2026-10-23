package me.tbsten.cream.test.architecture.tools.claudeCode.skills

import me.tbsten.cream.test.architecture.support.hasNoJapaneseTranslation
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.skills() =
    "Skills" {
        summary = "A file of a Claude Code skill of this repository. English only"
        example("cream-snapshot-test/SKILL.md")
        layout {
            ".claude/skills" / "*" / "**" / "*".file()
        }
        hasNoJapaneseTranslation()
    }
