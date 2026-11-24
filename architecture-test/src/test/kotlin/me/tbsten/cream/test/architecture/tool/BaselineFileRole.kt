package me.tbsten.cream.test.architecture.tool

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.baselineFileRole() =
    "BaselineFile".role {
        documented = false
        title = "katachi の baseline"
        summary = "katachi 導入時からある違反を一時的に許容する記録（katachi が書き出す）"
        description = "See: https://tbsten.github.io/katachi/guides/baseline/"
        example("katachi-baseline.json", "許容中の違反の一覧")

        layout {
            "katachi-baseline.json".file()
        }
    }
