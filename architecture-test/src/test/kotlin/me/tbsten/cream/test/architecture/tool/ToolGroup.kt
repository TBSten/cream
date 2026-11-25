package me.tbsten.cream.test.architecture.tool

import me.tbsten.cream.test.architecture.tool.aiAgent.aiAgentGroup
import me.tbsten.cream.test.architecture.tool.github.githubGroup
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.toolGroup() =
    "Tool".group {
        documented = false
        summary = "ビルド以外の開発ツールの設定（AI エージェント・Git・GitHub・ktlint・IDE・katachi など）"

        aiAgentGroup()
        gitIgnoreRole()
        githubGroup()
        editorConfigRole()
        ideRunConfigurationRole()
        baselineFileRole()
    }
