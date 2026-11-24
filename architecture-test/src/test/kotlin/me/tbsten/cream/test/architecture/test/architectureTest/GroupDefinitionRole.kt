@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.test.architectureTest

import me.tbsten.cream.test.architecture.ARCHITECTURE_DEFINITION_ROOT
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent

fun DeclarationContainerScope.groupDefinitionRole() =
    "GroupDefinition".role {
        documented = false
        title = "group の定義"
        summary = "architecture { } から分けた group 1 つ分の定義（<Name>Group.kt）"
        example("library/ksp/KspGroup.kt", "Ksp group")

        `ファイル名と同じ名前の拡張関数 1 つだけを置く`()

        layout {
            ARCHITECTURE_DEFINITION_ROOT / "**" / "*Group".ktFile()
        }
    }

private fun RoleScope.`ファイル名と同じ名前の拡張関数 1 つだけを置く`() =
    "ファイル名と同じ名前の拡張関数 1 つだけを置く".ktFileContent {
        `package`("me.tbsten.cream.test.architecture")
        `package`("me.tbsten.cream.test.architecture.**")
        fileAnnotation("OptIn")

        `import`("kotlin.**")
        `import`("me.tbsten.katachi.**")
        `import`("me.tbsten.cream.test.architecture.**")

        // fun DeclarationContainerScope.<name>Group() = "<Name>".group { ... }
        required(`fun`, fileName.replaceFirstChar { it.lowercase() })
    }
