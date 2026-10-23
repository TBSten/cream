package me.tbsten.cream.test.architecture.documentation.distributedSkill

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.distributedSkill() =
    "DistributedSkill" {
        summary = "An agent skill users copy into their own projects. English only, since agents read it"
        example("setup-cream/SKILL.md")
        layout {
            "doc/ai-integration/skills" / "*" / "*.md".file()
        }
    }
