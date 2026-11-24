package me.tbsten.cream.test.architecture.tool.github

import me.tbsten.cream.test.architecture.tool.github.actions.actionsGroup
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.githubGroup() =
    "Github".group {
        documented = false
        summary = "GitHub の設定（.github/）"

        actionsGroup()
    }
