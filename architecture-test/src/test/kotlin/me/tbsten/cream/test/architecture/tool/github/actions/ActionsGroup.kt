package me.tbsten.cream.test.architecture.tool.github.actions

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.actionsGroup() =
    "Actions".group {
        documented = false
        summary = "GitHub Actions"

        workflowRole()
    }
