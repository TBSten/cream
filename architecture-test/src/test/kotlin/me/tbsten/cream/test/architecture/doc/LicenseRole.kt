package me.tbsten.cream.test.architecture.doc

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.licenseRole() =
    "License".role {
        documented = false
        title = "ライセンス"
        summary = "プロジェクトのライセンス（Apache-2.0）"
        example("LICENSE", "Apache License 2.0")

        layout {
            "LICENSE".file()
        }
    }
