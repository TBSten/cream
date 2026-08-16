package me.tbsten.cream.konsist.dsl

import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoImportDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoObjectDeclaration
import com.lemonappdev.konsist.api.declaration.KoPackageDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.declaration.KoTypeAliasDeclaration

// このファイル: DSL の各語彙が emit する rule のファクトリ。
// 大筋は manifest（DSL 意味論）/ ManifestAssertionRule（rule の代数）を参照。

/** ktFile エントリ 1 つ分の標準 rule（存在の承認 + 中身・import・行数の検査）。 */
internal fun fileEntryRule(
    levelPrefix: String,
    matcher: FileMatcher,
    exactSiblingPaths: Set<String>,
    earlierPatterns: List<FileMatcher>,
    grants: List<TopLevelGrant>,
    importAllowances: List<ImportAllowance>,
    maxLines: Int,
): ManifestAssertionRule =
    ManifestAssertionRule { file ->
        val path = file.normalizedProjectPath
        if (!path.startsWith(levelPrefix)) return@ManifestAssertionRule AssertResult.NoEffect
        val relative = path.removePrefix(levelPrefix)
        if (!matcher.matches(relative)) return@ManifestAssertionRule AssertResult.NoEffect
        if (relative in exactSiblingPaths || earlierPatterns.any { it.matches(relative) }) {
            return@ManifestAssertionRule AssertResult.NoEffect
        }

        val violations = checkFile(file, grants, importAllowances, maxLines)
        if (violations.isEmpty()) AssertResult.Ok else AssertResult.Failure(violations)
    }

/** [ManifestEntriesBuilder.anyFiles] の受け皿 rule（行数上限だけは適用される）。 */
internal fun anyFilesRule(
    levelPrefix: String,
    siblingMatchers: List<FileMatcher>,
    siblingDirPaths: List<String>,
    maxLines: Int,
): ManifestAssertionRule =
    ManifestAssertionRule { file ->
        val path = file.normalizedProjectPath
        if (!path.startsWith(levelPrefix)) return@ManifestAssertionRule AssertResult.NoEffect
        val relative = path.removePrefix(levelPrefix)
        if (siblingMatchers.any { it.matches(relative) }) return@ManifestAssertionRule AssertResult.NoEffect
        if (siblingDirPaths.any { relative.startsWith("$it/") }) return@ManifestAssertionRule AssertResult.NoEffect

        val actualLines = file.text.lines().size
        if (actualLines > maxLines) {
            AssertResult.Failure(listOf("$actualLines 行 > 上限 $maxLines 行"))
        } else {
            AssertResult.Ok
        }
    }

/** package↔ディレクトリ一致の制約 rule（満足時は NoEffect — 存在の承認はしない）。 */
internal fun packageMatchesDirectoryRule(sourceRoot: String): ManifestAssertionRule =
    ManifestAssertionRule { file ->
        val path = file.normalizedProjectPath
        if (!path.startsWith(sourceRoot)) return@ManifestAssertionRule AssertResult.NoEffect
        val expected =
            path
                .removePrefix(sourceRoot)
                .substringBeforeLast('/', missingDelimiterValue = "")
                .replace('/', '.')
        val actual = file.packagee?.name.orEmpty()
        if (actual == expected) {
            AssertResult.NoEffect
        } else {
            AssertResult.Failure(listOf("package $actual ≠ ディレクトリ $expected（パス基準の manifest の前提が崩れる）"))
        }
    }

/** 要素 walk + 集計。ここが「manifest に書いてないことは書けない」の実体。 */
private fun checkFile(
    file: KoFileDeclaration,
    grants: List<TopLevelGrant>,
    importAllowances: List<ImportAllowance>,
    maxLines: Int,
): List<String> =
    buildList {
        // 全要素を検査対象にする。ここで対象を絞ると、絞りから漏れた要素が allow by default になる。
        file.declarations(includeNested = false, includeLocal = false).forEach { element ->
            when (element) {
                is KoPackageDeclaration -> Unit // package↔dir 一致は packageMatchesDirectoryRule が見る

                is KoImportDeclaration ->
                    if (importAllowances.none { it.matches(element.name) }) {
                        val allowed =
                            if (importAllowances.isEmpty()) {
                                "許可なし = import ゼロ（どの階層の imports にも許可が宣言されていない）"
                            } else {
                                importAllowances.joinToString { it.description }
                            }
                        add("import 許可（$allowed）に一致しない import: `import ${element.name}`")
                    }

                is KoAnnotationDeclaration ->
                    add(
                        "ファイルアノテーションはデフォルトで deny: `@file:${element.name}`" +
                            "（許可したい場合は人間が DSL に許可語彙を足す判断をすること）",
                    )

                is KoClassDeclaration,
                is KoInterfaceDeclaration,
                is KoObjectDeclaration,
                is KoFunctionDeclaration,
                is KoPropertyDeclaration,
                is KoTypeAliasDeclaration,
                ->
                    if (grants.none { it.matches(element) }) {
                        add("manifest のどの grant にも一致しないトップレベル宣言: `${element.render()}`")
                    }

                else ->
                    add(
                        "不明なトップレベル要素: ${element::class.simpleName} `${element.render()}` — " +
                            "DSL が未対応の Kotlin 言語機能。素通りさせず、人間が DSL の対応を判断すること",
                    )
            }
        }

        val declarations = file.topLevelDeclarations()
        grants.forEach { grant ->
            val matchCount = declarations.count { grant.matches(it) }
            when {
                grant.count != null && matchCount != grant.count ->
                    add("grant `${grant.description}` に一致する宣言は ${grant.count} 個のはずが $matchCount 個")

                grant.required && matchCount == 0 ->
                    add("必須の grant `${grant.description}` に一致する宣言がない")
            }
        }

        val actualLines = file.text.lines().size
        if (actualLines > maxLines) {
            add(
                "$actualLines 行 > 上限 $maxLines 行 — 責務を分割するか、" +
                    "分割できない理由があるなら人間に manifest の maxLines 変更を依頼すること",
            )
        }
    }
