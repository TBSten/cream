package me.tbsten.cream.test.architecture.doc

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.projectReadmeRole() =
    "ProjectReadme".role {
        documented = false
        title = "README"
        summary = "プロジェクトの README（英語版と日本語版）"
        example("README.md", "英語版 README")

        layout {
            "README.md".file()
            "README.ja.md".file()
        }
    }
