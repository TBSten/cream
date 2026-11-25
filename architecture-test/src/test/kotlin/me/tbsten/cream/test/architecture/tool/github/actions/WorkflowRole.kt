package me.tbsten.cream.test.architecture.tool.github.actions

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.workflowRole() =
    "Workflow".role {
        documented = false
        title = "GitHub Actions のワークフロー"
        summary = "CI・publish・Claude の GitHub Actions ワークフロー"
        example(".github/workflows/gradle.yml", "ビルドとテスト")

        layout {
            ".github" / "workflows" / "*.yml".file()
        }
    }
