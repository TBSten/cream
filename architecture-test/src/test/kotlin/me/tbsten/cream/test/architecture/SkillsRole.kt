package me.tbsten.cream.test.architecture

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.skillsRole() =
    "Skills".role {
        documented = false
        title = "利用者向け AI skill"
        summary = "利用者が gh skill install で自分の AI エージェントに入れる cream 用 skill の配布物"
        description = "skills/<skill>/ の中身は skill の形式が決めるので、配置は縛らない。"
        example("skills/cream/SKILL.md", "cream の導入・リファクタ skill")
        example("skills/cream/references/setup.md", "skill から読む導入手順")

        layout {
            "skills" / "*" / "**" / "*".file()
        }
    }
