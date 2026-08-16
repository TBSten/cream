package me.tbsten.cream.konsist.dsl

/**
 * manifest DSL の語彙（配置ツリー側）。自作語彙は [ManifestBuilder.module] 配下の
 * `manifest` / `dir` / `file` / `requiredFile` / `anyFiles`（+ 行数上限 `maxLines`）だけで、
 * ファイルの中身（トップレベル宣言の形）は file エントリの block で
 * `assertContent(`[fileContentManifest]`)` として必ず宣言する。
 *
 * 意味論（評価は [evaluate] が行う）— deny by default は配置と中身の両方に効く:
 * - 管轄区域（module + sourceSet）内で、どのエントリにも一致しない `.kt` は違反（deny by default）
 * - 宣言順で最初に一致したエントリが勝つ。特殊 → 一般の順に書く
 * - `file` は「あってよい」、`requiredFile` は「なければ違反」
 * - `file` / `requiredFile` は block を必ず取り（block なしはコンパイルエラー）、
 *   block 内で [FileEntryBuilder.assertContent] を呼ばなければ manifest 構築時に即例外
 * - `dir` も block を必ず取る。**空 block = 配下に `.kt` を置けない**。
 *   配置フリー（中身も自由）は [ManifestEntriesBuilder.anyFiles] の**明示**でのみ許可され、
 *   「エントリなし = 配置だけ許可」という暗黙モードはない
 * - エントリを持つ `dir` の配下は deny by default（後続の兄弟エントリには fallback しない）
 * - パスは完全一致（glob なし）。対象は `.kt` のみ
 *
 * Konsist の `assertTrue` / `assertFalse` / `assertNotEmpty` は `@Suppress("konsist.…")` を
 * 尊重して宣言を検査から除外してしまうため、この DSL の検証では使わない（OPEN-QUESTIONS Q22）。
 */
internal fun manifest(block: ManifestBuilder.() -> Unit): Manifest = ManifestBuilder().apply(block).build()

/** ファイル行数上限の既定値。`dir` ブロックの `maxLines` / file エントリの `maxLines` で上書きできる。 */
internal const val DEFAULT_MAX_LINES = 300

internal class Manifest(
    internal val sourceSets: List<SourceSetManifest>,
)

/** 1 つの管轄区域 = 1 モジュールの 1 source set。 */
internal class SourceSetManifest(
    internal val modulePath: String,
    internal val sourceSetName: String,
    internal val entries: List<ManifestEntry>,
    internal val allowsAnyFiles: Boolean,
) {
    /** 管轄区域のパス接頭辞（project root からの相対、区切りは `/`）。 */
    internal val rootPrefix: String =
        modulePath.removePrefix(":").replace(':', '/') + "/src/" + sourceSetName + "/"
}

internal sealed interface ManifestEntry {
    /** 親エントリからの相対パス。`/` 区切りで複数セグメント書ける。 */
    val relativePath: String
}

internal class FileEntry(
    override val relativePath: String,
    internal val required: Boolean,
    internal val maxLines: Int?,
    internal val content: FileContentManifest,
) : ManifestEntry

internal class DirEntry(
    override val relativePath: String,
    internal val maxLinesDefault: Int?,
    internal val entries: List<ManifestEntry>,
    internal val allowsAnyFiles: Boolean,
) : ManifestEntry

@DslMarker
internal annotation class ManifestDslMarker

@ManifestDslMarker
internal class ManifestBuilder internal constructor() {
    private val sourceSetBuilders = mutableListOf<SourceSetManifestBuilder>()

    /** `settings.gradle.kts` の include と同じ `":cream-ksp"` 形式でモジュールを指す。 */
    fun module(modulePath: String): ModuleManifestScope {
        require(modulePath.startsWith(":")) {
            "module path は \":cream-ksp\" 形式（settings.gradle.kts の include と同じ）で書く: $modulePath"
        }
        return ModuleManifestScope(modulePath, sourceSetBuilders)
    }

    internal fun build(): Manifest = Manifest(sourceSetBuilders.map { it.build() })
}

@ManifestDslMarker
internal class ModuleManifestScope internal constructor(
    private val modulePath: String,
    private val registry: MutableList<SourceSetManifestBuilder>,
) {
    /** このモジュールの 1 source set を管轄区域として宣言する。 */
    fun sourceSet(name: String): SourceSetManifestBuilder = SourceSetManifestBuilder(modulePath, name).also(registry::add)
}

@ManifestDslMarker
internal open class ManifestEntriesBuilder internal constructor() {
    private val entries = mutableListOf<ManifestEntry>()
    private var allowsAnyFiles = false

    /**
     * ディレクトリを許可する。block は必須で、配下は常に deny by default:
     * 空 block なら配下に `.kt` を 1 つも置けない。配置フリーにしたい場合は
     * block 内に [anyFiles] を**明示的に**書く（暗黙の緩和モードはない）。
     */
    fun dir(
        path: String,
        block: ManifestDirBuilder.() -> Unit,
    ) {
        entries += ManifestDirBuilder(validatePath(path)).apply(block).buildDir()
    }

    /**
     * あってもよいファイル。無くても違反にはならない。
     * block 内で [FileEntryBuilder.assertContent] を必ず呼ぶこと（呼ばなければ構築時に例外）。
     */
    fun file(
        path: String,
        block: FileEntryBuilder.() -> Unit,
    ) {
        entries += FileEntryBuilder(validateFilePath(path)).apply(block).build(required = false)
    }

    /**
     * 必須ファイル。管轄区域に存在しなければ違反。
     * block 内で [FileEntryBuilder.assertContent] を必ず呼ぶこと（呼ばなければ構築時に例外）。
     */
    fun requiredFile(
        path: String,
        block: FileEntryBuilder.() -> Unit,
    ) {
        entries += FileEntryBuilder(validateFilePath(path)).apply(block).build(required = true)
    }

    /**
     * この階層のどのエントリにも一致しない `.kt` をすべて許可する（配置フリー・中身も検査しない）。
     * 「manifest に書いてないことは書けない」の**明示的な**全緩和なので、使う場所には理由コメントを書くこと。
     * 位置によらず、同じ階層のほかのエントリをすべて試した後の受け皿として働く。
     */
    fun anyFiles() {
        allowsAnyFiles = true
    }

    internal fun buildEntries(): List<ManifestEntry> = entries.toList()

    internal fun buildAllowsAnyFiles(): Boolean = allowsAnyFiles

    private fun validatePath(path: String): String {
        require(path.isNotBlank() && !path.startsWith("/") && !path.endsWith("/") && "//" !in path) {
            "エントリのパスは空でなく、先頭・末尾の '/' や '//' を含まない相対パスで書く: \"$path\""
        }
        return path
    }

    private fun validateFilePath(path: String): String {
        require(validatePath(path).endsWith(".kt")) {
            "file / requiredFile のパスは .kt で終わる必要がある（manifest の対象は .kt のみ）: \"$path\""
        }
        return path
    }
}

@ManifestDslMarker
internal class ManifestDirBuilder internal constructor(
    private val path: String,
) : ManifestEntriesBuilder() {
    private var maxLinesDefault: Int? = null

    /** この dir 配下の既定行数上限を上書きする（file エントリの `maxLines` がさらに優先される）。 */
    fun maxLines(limit: Int) {
        require(limit > 0) { "maxLines は正の値で指定する: $limit" }
        maxLinesDefault = limit
    }

    internal fun buildDir(): DirEntry = DirEntry(path, maxLinesDefault, buildEntries(), buildAllowsAnyFiles())
}

/**
 * file / requiredFile エントリの block。[assertContent] の呼び出しが必須で、
 * 呼ばずに block を抜けると manifest 構築時に [IllegalStateException] を投げる —
 * 中身を宣言していない不完全な manifest はテスト実行前に必ず落ちる。
 */
@ManifestDslMarker
internal class FileEntryBuilder internal constructor(
    private val path: String,
) {
    private var maxLines: Int? = null
    private var content: FileContentManifest? = null

    /** このファイルの行数上限を上書きする（dir の `maxLines` 既定より優先される）。 */
    fun maxLines(limit: Int) {
        require(limit > 0) { "maxLines は正の値で指定する: $limit" }
        maxLines = limit
    }

    /**
     * このファイルのトップレベル宣言の目録（grant の列挙 = only 意味論）を宣言する。
     * どの grant にも一致しない宣言は違反になる。再利用する形は
     * `fun xxxFileContent() = fileContentManifest { … }` と名前付きで定義して渡す。
     */
    fun assertContent(content: FileContentManifest) {
        check(this.content == null) { "assertContent は 1 つの file エントリに 1 回だけ呼ぶ: \"$path\"" }
        this.content = content
    }

    /** 使い捨ての形をその場で書く [assertContent]。`assertContent(fileContentManifest { … })` の略記。 */
    fun assertContent(block: FileContentManifestBuilder.() -> Unit) {
        assertContent(fileContentManifest(block))
    }

    internal fun build(required: Boolean): FileEntry {
        val content =
            checkNotNull(content) {
                "content manifest 未指定: \"$path\" — file / requiredFile の block では " +
                    "assertContent(fileContentManifest { … }) を必ず呼び、トップレベル宣言の目録を宣言すること" +
                    "（存在のみ許可＝宣言ゼロなら assertContent(fileContentManifest { }) と明示する）"
            }
        return FileEntry(path, required, maxLines, content)
    }
}

@ManifestDslMarker
internal class SourceSetManifestBuilder internal constructor(
    private val modulePath: String,
    private val sourceSetName: String,
) : ManifestEntriesBuilder() {
    internal fun build(): SourceSetManifest = SourceSetManifest(modulePath, sourceSetName, buildEntries(), buildAllowsAnyFiles())
}
