package me.tbsten.cream.konsist.dsl

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

/**
 * [Manifest] の評価エンジン。scope 上の全ファイルから管轄区域（module + sourceSet）の `.kt` を
 * 切り出し、各ファイルをエントリと突き合わせて [FileVerdict] に分類する。
 *
 * ここは分類までしか行わない（kotest の assert は spec 側の責務）。分類結果は必ず
 * 「管轄区域の全ファイル = [SourceSetVerdict.matched] + [SourceSetVerdict.violations]」に
 * なるので、spec 側で件数を突き合わせれば「空リストで無言 PASS」を防げる。
 * 中身（トップレベル宣言の grant）の突き合わせは [FileVerdict.Matched.content] に対して
 * [FileContentManifest.findViolations] を spec 側から呼ぶ。
 */
internal fun Manifest.evaluate(files: List<KoFileDeclaration>): List<SourceSetVerdict> = sourceSets.map { it.evaluate(files) }

internal fun SourceSetManifest.evaluate(files: List<KoFileDeclaration>): SourceSetVerdict {
    val jurisdiction = files.filter { it.normalizedProjectPath.startsWith(rootPrefix) }
    val verdicts = jurisdiction.map { file -> matchFile(file) }
    val presentPaths = jurisdiction.map { it.normalizedProjectPath }.toSet()
    val missingRequired = requiredFilePaths(entries, rootPrefix).filterNot { it in presentPaths }
    return SourceSetVerdict(this, verdicts, missingRequired)
}

internal class SourceSetVerdict(
    internal val sourceSet: SourceSetManifest,
    /** 管轄区域の全 `.kt` の分類結果（1 ファイル 1 要素）。 */
    internal val files: List<FileVerdict>,
    /** `requiredFile` のうち管轄区域に存在しなかったものの project root からのパス。 */
    internal val missingRequiredFiles: List<String>,
) {
    internal val matched: List<FileVerdict.Matched> get() = files.filterIsInstance<FileVerdict.Matched>()
    internal val violations: List<FileVerdict.Unmatched> get() = files.filterIsInstance<FileVerdict.Unmatched>()
}

internal sealed interface FileVerdict {
    val file: KoFileDeclaration

    class Matched(
        override val file: KoFileDeclaration,
        /** 一致したエントリの project root からのパス（`anyFiles()` 経由なら受け入れた階層のパス）。 */
        internal val entryPath: String,
        /** このファイルに適用される行数上限（file エントリ > dir の maxLines > [DEFAULT_MAX_LINES]）。 */
        internal val maxLines: Int,
        /** file エントリが宣言する中身の目録。`anyFiles()` 経由の一致では null（中身も自由）。 */
        internal val content: FileContentManifest?,
    ) : FileVerdict

    class Unmatched(
        override val file: KoFileDeclaration,
        /** deny by default が発動した場所（一致した dir のパス。どの dir にも入らなければ管轄区域のルート）。 */
        internal val deniedAt: String,
    ) : FileVerdict
}

/** Konsist の `projectPath` を `/` 区切り・先頭区切りなしに正規化する（project root からの相対パス）。 */
internal val KoFileDeclaration.normalizedProjectPath: String
    get() = projectPath.replace('\\', '/').trimStart('/')

private fun SourceSetManifest.matchFile(file: KoFileDeclaration): FileVerdict =
    matchIn(
        entries = entries,
        allowsAnyFiles = allowsAnyFiles,
        relativePath = file.normalizedProjectPath.removePrefix(rootPrefix),
        matchedPrefix = rootPrefix,
        inheritedMaxLines = DEFAULT_MAX_LINES,
        file = file,
    )

private fun matchIn(
    entries: List<ManifestEntry>,
    allowsAnyFiles: Boolean,
    relativePath: String,
    matchedPrefix: String,
    inheritedMaxLines: Int,
    file: KoFileDeclaration,
): FileVerdict {
    for (entry in entries) {
        when (entry) {
            is FileEntry ->
                if (relativePath == entry.relativePath) {
                    return FileVerdict.Matched(
                        file = file,
                        entryPath = matchedPrefix + entry.relativePath,
                        maxLines = entry.maxLines ?: inheritedMaxLines,
                        content = entry.content,
                    )
                }

            is DirEntry -> {
                val dirPrefix = entry.relativePath + "/"
                if (relativePath.startsWith(dirPrefix)) {
                    // 宣言順で最初に一致した dir が勝つ: この dir の中で解決できなければ、
                    // 後続の兄弟エントリに fallback せずに違反とする（特殊 → 一般の順に書く）。
                    return matchIn(
                        entries = entry.entries,
                        allowsAnyFiles = entry.allowsAnyFiles,
                        relativePath = relativePath.removePrefix(dirPrefix),
                        matchedPrefix = matchedPrefix + dirPrefix,
                        inheritedMaxLines = entry.maxLinesDefault ?: inheritedMaxLines,
                        file = file,
                    )
                }
            }
        }
    }
    // どのエントリにも一致しなかった残りは、明示的な anyFiles() があるときだけ受け入れる
    // （中身は自由 = content なし）。なければこの階層で deny by default。
    return if (allowsAnyFiles) {
        FileVerdict.Matched(file = file, entryPath = matchedPrefix, maxLines = inheritedMaxLines, content = null)
    } else {
        FileVerdict.Unmatched(file = file, deniedAt = matchedPrefix)
    }
}

private fun requiredFilePaths(
    entries: List<ManifestEntry>,
    prefix: String,
): List<String> =
    entries.flatMap { entry ->
        when (entry) {
            is FileEntry -> if (entry.required) listOf(prefix + entry.relativePath) else emptyList()
            is DirEntry -> requiredFilePaths(entry.entries, prefix + entry.relativePath + "/")
        }
    }
