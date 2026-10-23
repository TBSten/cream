package me.tbsten.cream.test.architecture.documentation.customizationGuide

import me.tbsten.cream.test.architecture.support.hasJapaneseTranslation
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.customizationGuide() =
    "CustomizationGuide" {
        summary = "One page per customization point (options, funName, KDoc, visibility, ...)"
        example("options.md")
        layout {
            "doc/customization" / "*.md".file()
        }
        hasJapaneseTranslation()
    }
