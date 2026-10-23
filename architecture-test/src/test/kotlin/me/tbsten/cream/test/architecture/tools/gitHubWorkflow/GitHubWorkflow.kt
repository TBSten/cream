package me.tbsten.cream.test.architecture.tools.gitHubWorkflow

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.gitHubWorkflow() =
    "GitHubWorkflow" {
        summary = "A GitHub Actions workflow"
        example("gradle.yml")
        layout {
            ".github/workflows" / "*.yml".file()
        }
    }
