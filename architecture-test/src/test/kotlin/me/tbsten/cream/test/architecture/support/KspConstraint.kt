@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.support

import me.tbsten.cream.test.architecture.features
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.FileConstraintFailure
import me.tbsten.katachi.dsl.FileConstraintFix
import me.tbsten.katachi.dsl.FileConstraintReason
import me.tbsten.katachi.dsl.RoleScope
import me.tbsten.katachi.konsist.declarations.KtFileContentScope
import me.tbsten.katachi.konsist.declarations.ktFileContent
import java.io.File

// cream-ksp の main のファイルが共通で守る制約。各 Role から呼ぶ。
// （もとは cream-ksp のテストにあった Konsist の ArchTest / AllKotlinFilesTest の規則）

/** 1 ファイルの行数の上限。例外は [LINE_LIMIT_OVERRIDES] に理由とともに書く。 */
private const val MAX_FILE_LINES = 300

/**
 * [MAX_FILE_LINES] の例外（ファイル名 → 上限）。逃げ道なので小さく保つ。
 *
 * - FindMatchedProperty.kt: プロパティ照合（.Map / 名前一致 / デフォルト値）の分岐が密結合で、
 *   無理に割るとロジックが追いにくくなるため、上限 500 まで許容する。
 */
private val LINE_LIMIT_OVERRIDES =
    mapOf(
        "FindMatchedProperty.kt" to 500,
    )

internal fun RoleScope.`1 ファイル 300 行以内`() =
    fileConstraint("1 ファイル 300 行以内") { subject ->
        subject.files.mapNotNull { file ->
            val fileName = file.substringAfterLast('/')
            val limit = LINE_LIMIT_OVERRIDES[fileName] ?: MAX_FILE_LINES
            val lines = File("${subject.projectRoot}/$file").readLines().size
            if (lines <= limit) {
                null
            } else {
                FileConstraintFailure(
                    file = file,
                    reasons = listOf(FileConstraintReason("Too long", "$lines lines (limit $limit)")),
                    howToFix = listOf(FileConstraintFix("ファイルを分ける")),
                )
            }
        }
    }

/**
 * core 層（core/ 配下）が import してよいもの。feature 層と composition root
 * （ProcessContext / CreamSymbolProcessor / CreamSymbolProcessorProvider）は import できない。
 * core は ProcessContext 全体ではなく、必要な能力（options / logger など）を直接受け取る。
 */
internal fun KtFileContentScope.coreLayerImports() {
    `import`("kotlin.**")
    `import`("com.google.devtools.ksp.**")
    `import`("me.tbsten.cream.*")
    `import`("me.tbsten.cream.ksp.core.**")
    `import`("me.tbsten.cream.ksp.options.**")
    `import`("me.tbsten.cream.ksp.util.**")
}

/**
 * feature 層（feature/<feature>/ 配下）が import してよいもの。ほかの feature と、
 * composition root（CreamSymbolProcessor / CreamSymbolProcessorProvider）は import できない。
 * ProcessContext だけは import してよい。
 */
internal fun KtFileContentScope.featureLayerImports(feature: String) {
    `import`("kotlin.**")
    `import`("com.google.devtools.ksp.**")
    `import`("me.tbsten.cream.*")
    `import`("me.tbsten.cream.ksp.ProcessContext")
    `import`("me.tbsten.cream.ksp.core.**")
    `import`("me.tbsten.cream.ksp.options.**")
    `import`("me.tbsten.cream.ksp.util.**")
    `import`("me.tbsten.cream.ksp.feature.$feature.**")
}

internal fun RoleScope.`core 層は feature と composition root に依存しない`() =
    "core 層は feature と composition root に依存しない".ktFileContent {
        `package`("me.tbsten.cream.ksp.core.**")
        fileAnnotation("*")
        coreLayerImports()
        allow(anythingElse)
    }

internal fun RoleScope.`util は Kotlin だけに依存する`() =
    "util は Kotlin だけに依存する".ktFileContent {
        `package`("me.tbsten.cream.ksp.util")
        fileAnnotation("*")
        `import`("kotlin.**")
        allow(anythingElse)
    }

internal fun RoleScope.`KSP の util は KSP と Kotlin だけに依存する`() =
    "KSP の util は KSP と Kotlin だけに依存する".ktFileContent {
        `package`("me.tbsten.cream.ksp.util.ksp")
        fileAnnotation("*")
        `import`("kotlin.**")
        `import`("com.google.devtools.ksp.**")
        `import`("me.tbsten.cream.ksp.util.**")
        allow(anythingElse)
    }

internal fun RoleScope.`feature の補助はほかの feature と composition root に依存しない`() =
    "feature の補助はほかの feature と composition root に依存しない".ktFileContent {
        val feature = featureOf(fileName)
        `package`("me.tbsten.cream.ksp.feature.$feature")
        fileAnnotation("*")
        featureLayerImports(feature)
        allow(anythingElse)
    }

/**
 * ファイル名（ProcessCopyTo / CopyToChildrenDiagnostics など）が属する feature 名。
 * copyTo と copyToChildren のように前方一致が重なるときは、長い方を選ぶ。
 * 定義の読み込み時に fileName = "Sample" で 1 度呼ばれるので、見つからないときは空文字を返す。
 */
internal fun featureOf(fileName: String): String {
    val name = fileName.removePrefix("Process")
    return features
        .filter { name.startsWith(it.replaceFirstChar { c -> c.uppercase() }) }
        .maxByOrNull { it.length }
        ?: ""
}
