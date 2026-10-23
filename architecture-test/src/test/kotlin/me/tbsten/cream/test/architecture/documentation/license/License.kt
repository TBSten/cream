package me.tbsten.cream.test.architecture.documentation.license

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.license() =
    "License" {
        summary = "The MIT license text"
        example("LICENSE")
        layout {
            "LICENSE".file()
            "LICENSE.txt".file()
        }
    }
