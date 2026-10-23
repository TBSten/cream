package me.tbsten.cream.test.architecture.documentation.aiIntegrationGuide

import me.tbsten.cream.test.architecture.support.hasJapaneseTranslation
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.aiIntegrationGuide() =
    "AiIntegrationGuide" {
        summary = "How to use cream from AI agents"
        example("README.md")
        layout {
            "doc/ai-integration" / "*.md".file()
        }
        hasJapaneseTranslation()
    }
