package me.tbsten.cream.test.architecture.tools

import me.tbsten.cream.test.architecture.tools.claudeCode.claudeCode
import me.tbsten.cream.test.architecture.tools.editorConfig.editorConfig
import me.tbsten.cream.test.architecture.tools.gitHubWorkflow.gitHubWorkflow
import me.tbsten.cream.test.architecture.tools.gitIgnore.gitIgnore
import me.tbsten.cream.test.architecture.tools.runConfiguration.runConfiguration
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.tools() =
    "tools".group {
        title = "Tools"
        summary = "Configuration of the tools around the code: Git, ktlint, GitHub Actions, the IDE and AI agents"
        documented = false

        gitIgnore()
        editorConfig()
        gitHubWorkflow()
        runConfiguration()
        claudeCode()
    }
