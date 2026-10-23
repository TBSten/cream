package me.tbsten.cream.test.architecture.documentation.projectReadme

import me.tbsten.cream.test.architecture.support.hasJapaneseTranslation
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.projectReadme() =
    "ProjectReadme" {
        summary = "The README shown on GitHub, in English and Japanese"
        example("README.md")
        layout {
            "README.md".file()
            "README.ja.md".file()
        }
        hasJapaneseTranslation()
    }
