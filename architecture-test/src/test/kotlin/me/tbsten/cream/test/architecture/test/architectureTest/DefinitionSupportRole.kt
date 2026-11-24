package me.tbsten.cream.test.architecture.test.architectureTest

import me.tbsten.cream.test.architecture.ARCHITECTURE_DEFINITION_ROOT
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.definitionSupportRole() =
    "DefinitionSupport".role {
        documented = false
        title = "定義の共通部品"
        summary = "複数の Role から使う共通部品（support/）"
        example("support/PairedFileConstraint.kt", "対になるファイルがあることを求める fileConstraint")

        layout {
            ARCHITECTURE_DEFINITION_ROOT / "support" / "*".ktFile()
        }
    }
