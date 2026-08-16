package me.tbsten.cream.konsist.dsl

import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoBaseDeclaration
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoImportDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoObjectDeclaration
import com.lemonappdev.konsist.api.declaration.KoPackageDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.declaration.KoTypeAliasDeclaration
import com.lemonappdev.konsist.api.provider.KoNameProvider
import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider

/**
 * 1 ファイルのトップレベル宣言の目録（grant の列挙）を定義する。
 * [ManifestEntriesBuilder.file] / [ManifestEntriesBuilder.requiredFile] の block で
 * `assertContent(...)` に渡す（deny by default を中身にも効かせる仕組み）。
 *
 * 意味論は **only（許可の列挙）**:
 * - **どの grant にも一致しないトップレベル宣言は違反**。空の `fileContentManifest { }` は
 *   「宣言ゼロのみ許可」を意味する
 * - grant は「宣言の種類 × 名前 × 可視性 ×（任意で）個数」。単数形
 *   （[FileContentManifestBuilder.topLevelFunction] 等）は名前完全一致・必須・個数固定、
 *   複数形（[FileContentManifestBuilder.topLevelFunctions] 等）は名前パターンで任意個
 * - required / count は「その grant に一致した宣言の数」で検査する。1 つの宣言が複数の
 *   grant に一致してもよく、grant の宣言順に意味はない
 * - 明示的な緩和は [FileContentManifestBuilder.privateTopLevels]（private の補助宣言は任意個）と
 *   [FileContentManifestBuilder.anyTopLevel]（中身を問わない）のみ。使う場所には理由コメントを書くこと
 * - package / import 宣言は対象外。検査はトップレベルのみ（ネスト・ローカル宣言は見ない）
 */
internal fun fileContentManifest(block: FileContentManifestBuilder.() -> Unit): FileContentManifest = FileContentManifest(FileContentManifestBuilder().apply(block).build())

internal class FileContentManifest internal constructor(
    internal val grants: List<TopLevelGrant>,
) {
    /**
     * [file] のトップレベル宣言を grant と突き合わせ、違反メッセージのリストを返す
     * （assert はしない — kotest での検証は spec 側の責務）。
     */
    internal fun findViolations(file: KoFileDeclaration): List<String> {
        val declarations = file.topLevelDeclarations()
        val ungranted =
            declarations
                .filter { declaration -> grants.none { it.matches(declaration) } }
                .map { "manifest のどの grant にも一致しないトップレベル宣言: `${it.render()}`" }
        val unsatisfied =
            grants.mapNotNull { grant ->
                val matchCount = declarations.count { grant.matches(it) }
                when {
                    grant.count != null && matchCount != grant.count ->
                        "grant `${grant.description}` に一致する宣言は ${grant.count} 個のはずが $matchCount 個"

                    grant.required && matchCount == 0 ->
                        "必須の grant `${grant.description}` に一致する宣言がない"

                    else -> null
                }
            }
        return ungranted + unsatisfied
    }
}

/** トップレベル宣言の可視性。暗黙（修飾子なし）の public は [Public] として扱う。 */
internal enum class TopLevelVisibility(
    internal val keyword: String,
) {
    Public("public"),
    Internal("internal"),
    Private("private"),
}

/** grant が対象にできるトップレベル宣言の種類。enum class は [Class] とは別の種類として区別する。 */
internal enum class TopLevelKind(
    internal val keyword: String,
) {
    Class("class"),
    EnumClass("enum class"),
    Interface("interface"),
    Object("object"),
    Function("fun"),
    Property("val/var"),
    TypeAlias("typealias"),
}

/** 「宣言の種類 × 名前 × 可視性 ×（任意で）個数」の許可 1 件。`null` の軸は不問。 */
internal class TopLevelGrant internal constructor(
    private val kind: TopLevelKind?,
    private val name: String?,
    private val nameStartsWith: String?,
    private val nameEndsWith: String?,
    private val visibility: TopLevelVisibility?,
    internal val required: Boolean,
    internal val count: Int?,
    internal val description: String,
) {
    internal fun matches(declaration: KoBaseDeclaration): Boolean {
        if (kind != null && declaration.topLevelKind != kind) return false
        val declarationName = (declaration as? KoNameProvider)?.name
        if (name != null && declarationName != name) return false
        if (nameStartsWith != null && declarationName?.startsWith(nameStartsWith) != true) return false
        if (nameEndsWith != null && declarationName?.endsWith(nameEndsWith) != true) return false
        if (visibility != null && declaration.topLevelVisibility != visibility) return false
        return true
    }
}

@ManifestDslMarker
internal class FileContentManifestBuilder internal constructor() {
    private val grants = mutableListOf<TopLevelGrant>()

    // --- 単数形: 名前完全一致・必須・個数固定（既定はちょうど 1 つ） ---

    /** `class <name>` をちょうど 1 つ許可し、必須にする（enum class は一致しない — [topLevelEnumClass]）。 */
    fun topLevelClass(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
    ) = exact(TopLevelKind.Class, name, visibility, count = 1)

    /** `enum class <name>` をちょうど 1 つ許可し、必須にする。 */
    fun topLevelEnumClass(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
    ) = exact(TopLevelKind.EnumClass, name, visibility, count = 1)

    /** `interface <name>`（sealed / fun interface 含む）をちょうど 1 つ許可し、必須にする。 */
    fun topLevelInterface(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
    ) = exact(TopLevelKind.Interface, name, visibility, count = 1)

    /** `object <name>` をちょうど 1 つ許可し、必須にする。 */
    fun topLevelObject(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
    ) = exact(TopLevelKind.Object, name, visibility, count = 1)

    /** `typealias <name>` をちょうど 1 つ許可し、必須にする。 */
    fun topLevelTypeAlias(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
    ) = exact(TopLevelKind.TypeAlias, name, visibility, count = 1)

    /** `fun <name>` をちょうど [count] 個（オーバーロード数まで固定）許可し、必須にする。 */
    fun topLevelFunction(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
        count: Int = 1,
    ) = exact(TopLevelKind.Function, name, visibility, count = count)

    /** `val/var <name>` をちょうど [count] 個（拡張レシーバ違いまで固定）許可し、必須にする。 */
    fun topLevelProperty(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
        count: Int = 1,
    ) = exact(TopLevelKind.Property, name, visibility, count = count)

    // --- 複数形: 名前パターン。既定は任意個・任意（required / count で締める）。
    //     一致範囲を絞る軸（nameStartsWith / nameEndsWith / visibility）を最低 1 つ指定すること —
    //     全省略は実質 anyTopLevel と同じ暗黙の全緩和になるため構築時に拒否する ---

    /** 名前パターンに一致するトップレベル関数を許可する。 */
    fun topLevelFunctions(
        nameStartsWith: String? = null,
        nameEndsWith: String? = null,
        visibility: TopLevelVisibility? = null,
        required: Boolean = false,
        count: Int? = null,
    ) = pattern(TopLevelKind.Function, nameStartsWith, nameEndsWith, visibility, required, count)

    /** 名前パターンに一致するトップレベルクラスを許可する。 */
    fun topLevelClasses(
        nameStartsWith: String? = null,
        nameEndsWith: String? = null,
        visibility: TopLevelVisibility? = null,
        required: Boolean = false,
        count: Int? = null,
    ) = pattern(TopLevelKind.Class, nameStartsWith, nameEndsWith, visibility, required, count)

    // --- 明示的な緩和（使う場所には理由コメントを書くこと） ---

    /** private のトップレベル宣言（補助宣言）を種類・名前を問わず任意個許可する。 */
    fun privateTopLevels() {
        grants +=
            TopLevelGrant(
                kind = null,
                name = null,
                nameStartsWith = null,
                nameEndsWith = null,
                visibility = TopLevelVisibility.Private,
                required = false,
                count = null,
                description = "privateTopLevels()",
            )
    }

    /** すべてのトップレベル宣言を許可する（中身を問わない、の明示）。 */
    fun anyTopLevel() {
        grants +=
            TopLevelGrant(
                kind = null,
                name = null,
                nameStartsWith = null,
                nameEndsWith = null,
                visibility = null,
                required = false,
                count = null,
                description = "anyTopLevel()",
            )
    }

    internal fun build(): List<TopLevelGrant> = grants.toList()

    private fun exact(
        kind: TopLevelKind,
        name: String,
        visibility: TopLevelVisibility,
        count: Int,
    ) {
        require(count > 0) { "count は正の値で指定する: $count（$name）" }
        grants +=
            TopLevelGrant(
                kind = kind,
                name = name,
                nameStartsWith = null,
                nameEndsWith = null,
                visibility = visibility,
                required = true,
                count = count,
                description = "${visibility.keyword} ${kind.keyword} $name" + if (count != 1) " ×$count" else "",
            )
    }

    private fun pattern(
        kind: TopLevelKind,
        nameStartsWith: String?,
        nameEndsWith: String?,
        visibility: TopLevelVisibility?,
        required: Boolean,
        count: Int?,
    ) {
        require(count == null || count > 0) { "count は正の値で指定する: $count" }
        require(nameStartsWith != null || nameEndsWith != null || visibility != null) {
            "パターン grant は一致範囲を絞る軸（nameStartsWith / nameEndsWith / visibility）を最低 1 つ指定する — " +
                "すべての${kind.keyword}を許可したい場合は anyTopLevel() を明示的に使うこと"
        }
        val conditions =
            listOfNotNull(
                nameStartsWith?.let { "nameStartsWith = \"$it\"" },
                nameEndsWith?.let { "nameEndsWith = \"$it\"" },
                visibility?.let { "visibility = ${it.name}" },
                if (required) "required" else null,
                count?.let { "count = $it" },
            )
        grants +=
            TopLevelGrant(
                kind = kind,
                name = null,
                nameStartsWith = nameStartsWith,
                nameEndsWith = nameEndsWith,
                visibility = visibility,
                required = required,
                count = count,
                description = "${kind.keyword}(${conditions.joinToString(", ")})",
            )
    }
}

/**
 * トップレベル宣言のみを返す。Konsist の `declarations()` は package 宣言・import 宣言に加え、
 * ファイルアノテーション（`@file:OptIn` 等）も「宣言」として含むため、それらを除外する。
 */
internal fun KoFileDeclaration.topLevelDeclarations(): List<KoBaseDeclaration> =
    declarations(includeNested = false, includeLocal = false)
        .filterNot { it is KoPackageDeclaration || it is KoImportDeclaration || it is KoAnnotationDeclaration }

/** 違反メッセージ用に宣言を `internal fun name` の形で描画する。 */
internal fun KoBaseDeclaration.render(): String {
    val visibility = topLevelVisibility?.keyword?.plus(" ").orEmpty()
    val kind = topLevelKind?.keyword ?: "declaration"
    val name = (this as? KoNameProvider)?.name ?: toString()
    return "$visibility$kind $name"
}

private val KoBaseDeclaration.topLevelKind: TopLevelKind?
    get() =
        when (this) {
            is KoInterfaceDeclaration -> TopLevelKind.Interface
            is KoObjectDeclaration -> TopLevelKind.Object
            is KoClassDeclaration -> if (hasEnumModifier) TopLevelKind.EnumClass else TopLevelKind.Class
            is KoFunctionDeclaration -> TopLevelKind.Function
            is KoPropertyDeclaration -> TopLevelKind.Property
            is KoTypeAliasDeclaration -> TopLevelKind.TypeAlias
            else -> null
        }

private val KoBaseDeclaration.topLevelVisibility: TopLevelVisibility?
    get() =
        (this as? KoVisibilityModifierProvider)?.let {
            when {
                it.hasPrivateModifier -> TopLevelVisibility.Private
                it.hasInternalModifier -> TopLevelVisibility.Internal
                it.hasPublicOrDefaultModifier -> TopLevelVisibility.Public
                // protected はトップレベルに書けないので実質到達しない
                else -> null
            }
        }
