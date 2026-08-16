package me.tbsten.cream.konsist.dsl

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

/**
 * manifest DSL の語彙。**DSL 意味論の大筋はここに書く**（rule の代数の大筋は [ManifestAssertionRule]）。
 *
 * DSL の関数が呼ばれるたびに rule が生成され、[Manifest.assertionRule] に [plus] で積まれていく
 * （DSL を写した中間データは持たない）。deny by default は配置・中身・import のすべてに効く:
 *
 * - 管轄区域（module + sourceSet）内で、どの rule も承認しないファイルは配置違反
 *   （[Manifest.assert] の finalize が確定させる）
 * - **具体名のエントリはパターン（`ktFile("*.kt")`）より常に優先される**（書き順に依存しない）。
 *   パターン同士が重なる場合だけ宣言順で最初のものが勝つ
 * - `ktFile` は既定で「あってよい」、`ktFile(required = true)` は「なければ違反」（存在検査は
 *   [Manifest.requiredPaths] 経由で spec が集合レベルで行う — 存在しないファイルは per-file の
 *   rule に現れないため）
 * - `ktFile` の block がそのままそのファイルの目録: トップレベル宣言の grant と
 *   [KtFileBuilder.imports] を列挙する。**空 block = トップレベル宣言ゼロ・import ゼロのみ許可**
 * - `dir` は block 必須。**空 block = 配下に `.kt` を置けない**。配置フリー（中身も自由）は
 *   [ManifestEntriesBuilder.anyFiles] の**明示**でのみ許可される
 * - **import は deny by default**: 許可は各 `ktFile` の block（[KtFileBuilder.imports]）にだけ書ける
 *   （dir / sourceSet 階層には書けない — ktFile の block だけでそのファイルの許可全量が分かる）。
 *   宣言が無ければ import ゼロのみ許可
 * - **[ManifestEntriesBuilder.maxLines] は同じ block 内のエントリ（dir / ktFile / anyFiles）より前に
 *   宣言する**（後置は構築時に例外 — rule は宣言時点の文脈で作られるため、後から書いた宣言が
 *   黙って効かない事故を防ぐ）
 * - パスは完全一致。例外として **`ktFile` のファイル名部分にだけ `*` を書ける**
 *   （ディレクトリ部分と `required = true` は常に完全一致）。対象は `.kt` のみ
 *
 * Konsist の `assertTrue` / `assertFalse` / `assertNotEmpty` は `@Suppress("konsist.…")` を
 * 尊重して宣言を検査から除外してしまうため、この DSL の検証では使わない（OPEN-QUESTIONS Q22）。
 */
internal fun manifest(block: ManifestBuilder.() -> Unit): Manifest = ManifestBuilder().apply(block).build()

/** ファイル行数上限の既定値。[ManifestEntriesBuilder.maxLines] / [KtFileBuilder.maxLines] で上書きできる。 */
internal const val DEFAULT_MAX_LINES = 300

internal class Manifest private constructor(
    /** manifest 全体を 1 本に合成した rule（唯一の公開面）。 */
    val assertionRule: ManifestAssertionRule,
    /** `ktFile(required = true)` の project root からのパス。存在検査は spec が集合レベルで行う。 */
    val requiredPaths: List<String>,
    private val rootPrefixes: List<String>,
) {
    /** rule を 1 つ積んだ新しい Manifest（immutable 更新）。 */
    operator fun plus(rule: ManifestAssertionRule): Manifest = Manifest(assertionRule + rule, requiredPaths, rootPrefixes)

    internal fun withRequiredPath(path: String): Manifest = Manifest(assertionRule, requiredPaths + path, rootPrefixes)

    internal fun withRootPrefix(prefix: String): Manifest = Manifest(assertionRule, requiredPaths, rootPrefixes + prefix)

    /** 管轄区域（全 sourceSet の配下）に属するファイルだけを返す。 */
    fun scopeFiles(allFiles: List<KoFileDeclaration>): List<KoFileDeclaration> = allFiles.filter { file -> rootPrefixes.any { file.normalizedProjectPath.startsWith(it) } }

    /** [file] を [assertionRule] で検査し、未確定（誰も承認しない）は配置違反へ確定させる。 */
    fun assert(file: KoFileDeclaration): AssertResult.Final =
        assertionRule.assert(file).finalize {
            AssertResult.Failure(
                listOf(
                    "配置: ${file.nameWithExtension} は manifest のどの rule の対象でもない" +
                        "（deny by default — 列挙されていない場所に .kt を置くことはできない）",
                ),
            )
        }

    companion object {
        internal val EMPTY = Manifest(nothingRule, emptyList(), emptyList())
    }
}

/** Konsist の `projectPath` を `/` 区切り・先頭区切りなしに正規化する（project root からの相対パス）。 */
internal val KoFileDeclaration.normalizedProjectPath: String
    get() = projectPath.replace('\\', '/').trimStart('/')

@DslMarker
internal annotation class ManifestDslMarker

/**
 * DSL の表層。内部状態は immutable な [Manifest] 1 つだけで、DSL の各語彙が rule へ変換されるたびに
 * `manifest = manifest + rule` で置き換わっていく（DSL を写す中間データは持たない）。
 */
@ManifestDslMarker
internal class ManifestBuilder internal constructor() {
    private var manifest: Manifest = Manifest.EMPTY

    /** `settings.gradle.kts` の include と同じ `":cream-ksp"` 形式でモジュールを指す。 */
    fun module(modulePath: String): ModuleManifestScope {
        require(modulePath.startsWith(":")) {
            "module path は \":cream-ksp\" 形式（settings.gradle.kts の include と同じ）で書く: $modulePath"
        }
        return ModuleManifestScope(this, modulePath)
    }

    internal fun emit(newRule: ManifestAssertionRule) {
        manifest += newRule
    }

    internal fun emitRequiredPath(path: String) {
        manifest = manifest.withRequiredPath(path)
    }

    internal fun registerRootPrefix(prefix: String) {
        manifest = manifest.withRootPrefix(prefix)
    }

    internal fun build(): Manifest = manifest
}

@ManifestDslMarker
internal class ModuleManifestScope internal constructor(
    private val sink: ManifestBuilder,
    private val modulePath: String,
) {
    /** このモジュールの 1 source set を管轄区域として宣言する。 */
    fun sourceSet(name: String): SourceSetManifestBuilder {
        val rootPrefix = modulePath.removePrefix(":").replace(':', '/') + "/src/" + name + "/"
        sink.registerRootPrefix(rootPrefix)
        sink.emit(packageMatchesDirectoryRule(sourceRoot = rootPrefix + "kotlin/"))
        return SourceSetManifestBuilder(sink, rootPrefix)
    }
}

@ManifestDslMarker
internal open class ManifestEntriesBuilder internal constructor(
    private val sink: ManifestBuilder,
    private val levelPrefix: String,
    private val inheritedMaxLines: Int,
) {
    // rule の closure が遅延参照するレベル内レジストリ（構築完了後の全量を rule 評価時に見る）
    private val exactPaths = mutableSetOf<String>()
    private val allMatchers = mutableListOf<FileMatcher>()
    private val patternMatchers = mutableListOf<FileMatcher>()
    private val dirPaths = mutableListOf<String>()

    private var entriesStarted = false
    private var levelMaxLines: Int? = null

    private val effectiveMaxLines: Int
        get() = levelMaxLines ?: inheritedMaxLines

    /** この階層配下の既定行数上限を上書きする（[KtFileBuilder.maxLines] がさらに優先される）。 */
    fun maxLines(limit: Int) {
        check(!entriesStarted) { "maxLines は同じ block のエントリより前に宣言する（後置は黙って効かない事故のもと）" }
        require(limit > 0) { "maxLines は正の値で指定する: $limit" }
        levelMaxLines = limit
    }

    /** ディレクトリを許可する。block 必須・空 block = 配下に `.kt` を置けない。 */
    fun dir(
        path: String,
        block: ManifestDirBuilder.() -> Unit,
    ) {
        validatePath(path)
        entriesStarted = true
        val overlapping = exactPaths.firstOrNull { it.startsWith("$path/") }
        require(overlapping == null) {
            "dir(\"$path\") は ktFile(\"$overlapping\") と重なる — ktFile エントリを dir の block の中に移すこと"
        }
        dirPaths += path
        ManifestDirBuilder(sink, "$levelPrefix$path/", effectiveMaxLines).apply(block)
    }

    /**
     * ファイル 1 つ（または `*` パターンに一致するファイル群）の目録。
     * [required] `= false`（既定）は「あってもよい」、`= true` は「なければ違反」。
     * `*` はファイル名部分にだけ書ける（[required] `= true` との併用は不可 —
     * 「何が必須か」は具体名で指す）。
     */
    fun ktFile(
        path: String,
        required: Boolean = false,
        block: KtFileBuilder.() -> Unit,
    ) {
        addFileEntry(path, required = required, block = block)
    }

    /**
     * この階層のどのエントリにも一致しない `.kt` をすべて許可する（配置フリー・中身も検査しない。
     * 行数上限だけは適用される）。明示の全緩和なので、使う場所には理由コメントを書くこと。
     */
    fun anyFiles() {
        entriesStarted = true
        sink.emit(
            anyFilesRule(
                levelPrefix = levelPrefix,
                siblingMatchers = allMatchers,
                siblingDirPaths = dirPaths,
                maxLines = effectiveMaxLines,
            ),
        )
    }

    private fun addFileEntry(
        path: String,
        required: Boolean,
        block: KtFileBuilder.() -> Unit,
    ) {
        validateFilePath(path, allowPattern = !required)
        entriesStarted = true
        val overlapping = dirPaths.firstOrNull { path.startsWith("$it/") }
        require(overlapping == null) {
            "ktFile(\"$path\") は dir(\"$overlapping\") と重なる — ktFile エントリを dir の block の中に移すこと"
        }
        val matcher = FileMatcher(path)
        if (!matcher.isPattern) {
            require(exactPaths.add(path)) {
                "同じパスの ktFile エントリが重複している: \"$path\" — 1 ファイルの目録は 1 箇所にまとめること"
            }
        }
        val builder = KtFileBuilder().apply(block)
        sink.emit(
            fileEntryRule(
                levelPrefix = levelPrefix,
                matcher = matcher,
                // 具体名がパターンに常に勝つ（書き順非依存）: exactPaths は可変参照で渡し、
                // 後から宣言される具体名も rule 評価時（構築完了後）には見えている
                exactSiblingPaths = if (matcher.isPattern) exactPaths else emptySet(),
                // パターン同士は宣言順で最初が勝つ: この時点までのパターンのスナップショット
                earlierPatterns = if (matcher.isPattern) patternMatchers.toList() else emptyList(),
                grants = builder.buildGrants(),
                importAllowances = builder.buildImportAllowances(),
                maxLines = builder.buildMaxLines() ?: effectiveMaxLines,
            ),
        )
        allMatchers += matcher
        if (matcher.isPattern) patternMatchers += matcher
        if (required) sink.emitRequiredPath(levelPrefix + path)
    }

    private fun validatePath(path: String) {
        require(path.isNotBlank() && !path.startsWith("/") && !path.endsWith("/") && "//" !in path) {
            "エントリのパスは空でなく、先頭・末尾の '/' や '//' を含まない相対パスで書く: \"$path\""
        }
    }

    private fun validateFilePath(
        path: String,
        allowPattern: Boolean,
    ) {
        validatePath(path)
        require(path.endsWith(".kt")) {
            "ktFile のパスは .kt で終わる必要がある（manifest の対象は .kt のみ）: \"$path\""
        }
        require(path.substringBeforeLast('/', "").none { it == '*' }) {
            "パターン `*` はファイル名部分にだけ書ける（ディレクトリは具体名で列挙する）: \"$path\""
        }
        require(allowPattern || '*' !in path) {
            "ktFile(required = true) にパターン `*` は使えない（必須の対象は具体的なファイル名で指す）: \"$path\""
        }
    }
}

/** dir の block。中身は [ManifestEntriesBuilder] と同じ（名前は zone 関数のレシーバとして安定させるため残す）。 */
@ManifestDslMarker
internal class ManifestDirBuilder internal constructor(
    sink: ManifestBuilder,
    levelPrefix: String,
    inheritedMaxLines: Int,
) : ManifestEntriesBuilder(sink, levelPrefix, inheritedMaxLines)

/** sourceSet 直下の block。中身は [ManifestEntriesBuilder] と同じ。 */
@ManifestDslMarker
internal class SourceSetManifestBuilder internal constructor(
    sink: ManifestBuilder,
    rootPrefix: String,
) : ManifestEntriesBuilder(sink, rootPrefix, DEFAULT_MAX_LINES)

/** ktFile エントリのパス一致（`*` はファイル名部分の任意文字列、`/` を跨がない）。 */
internal class FileMatcher(
    internal val path: String,
) {
    private val dirPart: String = path.substringBeforeLast('/', "")
    private val fileNamePattern: Regex? =
        path.substringAfterLast('/').let { name ->
            if ('*' in name) {
                name.split('*').joinToString(separator = "[^/]*") { Regex.escape(it) }.toRegex()
            } else {
                null
            }
        }

    internal val isPattern: Boolean get() = fileNamePattern != null

    internal fun matches(relative: String): Boolean {
        if (fileNamePattern == null) return relative == path
        val slash = relative.lastIndexOf('/')
        val dir = if (slash >= 0) relative.substring(0, slash) else ""
        val fileName = relative.substring(slash + 1)
        return dir == dirPart && fileNamePattern.matches(fileName)
    }
}
