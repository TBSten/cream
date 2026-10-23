package me.tbsten.cream.test.architecture.documentation.context7Config

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.context7Config() =
    "Context7Config" {
        summary = "Configuration of the Context7 documentation index"
        documented = false
        example("context7.json")
        layout {
            "context7.json".file()
        }
    }
