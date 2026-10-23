package me.tbsten.cream.test.architecture.documentation.featureGuide

import me.tbsten.cream.test.architecture.support.hasJapaneseTranslation
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.featureGuide() =
    "FeatureGuide" {
        summary = "One page per feature or topic (copy, combine, sealed copy, comparison, ...), linked from the README"
        example("copy.md")
        layout {
            "doc" / "*.md".file()
        }
        hasJapaneseTranslation()
    }
