package me.tbsten.cream.test.architecture.tools.runConfiguration

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.runConfiguration() =
    "RunConfiguration" {
        summary = "A shared IntelliJ IDEA run configuration"
        example("_cream-ksp_test.run.xml")
        layout {
            ".run" / "*.run.xml".file()
        }
    }
