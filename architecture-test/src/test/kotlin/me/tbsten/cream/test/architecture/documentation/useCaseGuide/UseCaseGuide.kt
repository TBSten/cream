package me.tbsten.cream.test.architecture.documentation.useCaseGuide

import me.tbsten.cream.test.architecture.support.hasJapaneseTranslation
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.useCaseGuide() =
    "UseCaseGuide" {
        summary = "A worked example of cream in real code; the snapshot tests reproduce it in `scenario/UseCases.kt`"
        example("model-mapping.md")
        layout {
            "doc/use-case" / "**" / "*.md".file()
        }
        hasJapaneseTranslation()
    }
