package me.tbsten.cream.test.architecture.documentation.featureOptionMatrix

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.featureOptionMatrix() =
    "FeatureOptionMatrix" {
        summary = "The internal reference of which option affects which feature: the Markdown source and its rendered HTML"
        example("feature-and-options.md")
        layout {
            "doc/internal" {
                "feature-and-options.md".file()
                "feature-and-options.html".file()
            }
        }
    }
