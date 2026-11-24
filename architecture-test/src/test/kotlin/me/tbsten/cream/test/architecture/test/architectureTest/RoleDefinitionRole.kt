@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.test.architectureTest

import me.tbsten.cream.test.architecture.ARCHITECTURE_DEFINITION_ROOT
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.declarations.ktFileContent

fun DeclarationContainerScope.roleDefinitionRole() =
    "RoleDefinition".role {
        documented = false
        title = "Role の定義"
        summary = "architecture { } から分けた Role 1 つ分の定義（<Name>Role.kt）"
        example("library/ksp/FeatureProcessRole.kt", "FeatureProcess Role")

        `ファイル名と同じ名前の拡張関数 1 つと private な補助だけを置く`()

        layout {
            ARCHITECTURE_DEFINITION_ROOT / "**" / "*Role".ktFile()
        }
    }

private fun RoleScope.`ファイル名と同じ名前の拡張関数 1 つと private な補助だけを置く`() =
    "ファイル名と同じ名前の拡張関数 1 つと private な補助だけを置く".ktFileContent {
        `package`("me.tbsten.cream.test.architecture")
        `package`("me.tbsten.cream.test.architecture.**")
        fileAnnotation("OptIn")

        `import`("kotlin.**")
        `import`("me.tbsten.katachi.**")
        `import`("me.tbsten.cream.test.architecture.**")
        `import`("java.io.File")

        // fun DeclarationContainerScope.<name>Role() = "<Name>".role { ... }
        required(`fun`, fileName.replaceFirstChar { it.lowercase() })
        // ktFileContent / fileConstraint を切り出した private fun
        allow(private, `fun`, "*")
    }
