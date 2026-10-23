package me.tbsten.cream.test.architecture.documentation.docImage

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.docImage() =
    "DocImage" {
        summary = "An image embedded in the documentation"
        example("cream.copyFunNamingStrategy.png")
        layout {
            "doc" / "*.png".file()
        }
    }
