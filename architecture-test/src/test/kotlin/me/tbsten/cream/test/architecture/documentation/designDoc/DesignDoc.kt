package me.tbsten.cream.test.architecture.documentation.designDoc

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.designDoc() =
    "DesignDoc" {
        summary = "An internal design note, one per issue (`<issue number>-<topic>.md`)"
        example("135-parent-optional.md")
        layout {
            "doc/internal/design" / "*-*.md".file()
        }
    }
