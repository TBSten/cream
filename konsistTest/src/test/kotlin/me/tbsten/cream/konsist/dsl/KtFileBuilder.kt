package me.tbsten.cream.konsist.dsl

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

/** import 1 件の許可。[description] は違反メッセージの「許可一覧」に使われる。 */
internal class ImportAllowance internal constructor(
    internal val description: String,
    private val matcher: (String) -> Boolean,
) {
    internal fun matches(importName: String): Boolean = matcher(importName)
}

/** [KtFileBuilder.imports] の block。許可するパッケージを列挙する。 */
@ManifestDslMarker
internal class ImportsBuilder internal constructor() {
    private val allowances = mutableListOf<ImportAllowance>()

    /** [prefix] パッケージ配下（自身含む・**サブパッケージも含む**）の import を許可する。wildcard import（bare package 名）も一致する。 */
    fun packageTree(prefix: String) {
        validatePackageName("packageTree", prefix)
        allowances += ImportAllowance("$prefix..") { it == prefix || it.startsWith("$prefix.") }
    }

    /**
     * [packageName] パッケージ**直下**のシンボルの import だけを許可する（サブパッケージは含まない —
     * 広げたい場合は [packageTree]）。wildcard import（bare package 名）も一致する。
     * 注意: 判定は文字列のみなので、直下クラスのネスト型の import（`pkg.Outer.Nested`）は
     * 「さらに '.' が続く」ため一致しない。必要になったらそのネスト型を個別に許可すること。
     */
    fun packageEquals(packageName: String) {
        validatePackageName("packageEquals", packageName)
        allowances +=
            ImportAllowance("$packageName.*") { imported ->
                imported == packageName ||
                    (imported.startsWith("$packageName.") && '.' !in imported.removePrefix("$packageName."))
            }
    }

    /** 完全修飾名 [name] の import だけを許可する（1 型だけ開けたいとき。[packageEquals] より狭い）。 */
    fun fqName(name: String) {
        validatePackageName("fqName", name)
        allowances += ImportAllowance(name) { it == name }
    }

    internal fun build(): List<ImportAllowance> = allowances.toList()

    private fun validatePackageName(
        vocabulary: String,
        packageName: String,
    ) {
        require(packageName.isNotBlank() && !packageName.startsWith(".") && !packageName.endsWith(".")) {
            "$vocabulary はパッケージ名で書く（先頭・末尾の '.' なし）: \"$packageName\""
        }
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

/**
 * トップレベル宣言の許可 1 件。一致判定は語彙関数が合成した [matcher]（述語）に委ね、
 * この型は集計に使うメタデータ（[required] / [count] / [description]）だけを持つ
 * （[ImportAllowance] と同じ構え）。軸が増えても述語を 1 つ足すだけで、この型は変わらない。
 */
internal class TopLevelGrant internal constructor(
    internal val description: String,
    internal val required: Boolean,
    internal val count: Int?,
    private val matcher: (KoBaseDeclaration) -> Boolean,
) {
    internal fun matches(declaration: KoBaseDeclaration): Boolean = matcher(declaration)
}

// --- 軸ごとの述語。`null` の軸は合成に加わらない（= 不問） ---

private fun allOf(vararg predicates: ((KoBaseDeclaration) -> Boolean)?): (KoBaseDeclaration) -> Boolean {
    val active = predicates.filterNotNull()
    return { declaration -> active.all { it(declaration) } }
}

private fun kindIs(kind: TopLevelKind): (KoBaseDeclaration) -> Boolean = { it.topLevelKind == kind }

private fun nameEqualsTo(name: String): (KoBaseDeclaration) -> Boolean = { (it as? KoNameProvider)?.name == name }

private fun namePrefixed(prefix: String): (KoBaseDeclaration) -> Boolean = { (it as? KoNameProvider)?.name?.startsWith(prefix) == true }

private fun nameSuffixed(suffix: String): (KoBaseDeclaration) -> Boolean = { (it as? KoNameProvider)?.name?.endsWith(suffix) == true }

private fun visibilityIn(visibilities: Set<TopLevelVisibility>): (KoBaseDeclaration) -> Boolean = { it.topLevelVisibility in visibilities }

/** 関数専用の軸: 関数以外の宣言には一致しない。 */
private fun functionWith(condition: (KoFunctionDeclaration) -> Boolean): (KoBaseDeclaration) -> Boolean =
    { declaration -> (declaration as? KoFunctionDeclaration)?.let(condition) == true }

/** クラス専用の軸: クラス以外の宣言には一致しない。 */
private fun classWith(condition: (KoClassDeclaration) -> Boolean): (KoBaseDeclaration) -> Boolean = { declaration -> (declaration as? KoClassDeclaration)?.let(condition) == true }

/** [name] を（間接も含めて）継承しているクラスにだけ一致する。 */
private fun extendsClass(name: String) = classWith { klass -> klass.parents(indirectParents = true).any { it.name == name } }

private fun receiverIs(receiver: String) = functionWith { it.receiverType?.name == receiver }

private fun returnsType(returns: String) = functionWith { it.returnTypeNameOrUnit == returns }

private fun contextParametersAre(types: Set<String>) = functionWith { it.contextParameterTypeNames == types }

/** 戻り値型の名前。省略（block body で型注釈なし）は `Unit` として扱う。 */
private val KoFunctionDeclaration.returnTypeNameOrUnit: String
    get() = returnType?.name ?: "Unit"

/**
 * `context(...)` 節の型名の集合。Konsist 0.17.x は context parameters をモデル化しておらず、
 * さらに **[KoFunctionDeclaration] の `text` は context 節（と KDoc）を含まない**（実測: `text` は
 * `internal fun …` 行から始まる）。そのためファイル本文を [KoFunctionDeclaration] の location 行から
 * **上向きに遡り**、アノテーション行はまたぎつつ `context(...)` 行を拾う。KDoc の終端行や空行に
 * 到達したら停止するので、KDoc 内の例文に `context(` が現れても誤検知しない。
 * 制約: `context(...)` 節は 1 行で書かれている前提（cream の規約どおり）。
 */
private val KoFunctionDeclaration.contextParameterTypeNames: Set<String>
    get() {
        // location は "path:行:桁"（行は 1-based で `fun` 宣言行を指す）
        val declarationLine =
            location
                .substringBeforeLast(':')
                .substringAfterLast(':')
                .toIntOrNull() ?: return emptySet()
        val fileLines = containingFile.text.lines()
        val result = mutableSetOf<String>()
        var index = declarationLine - 2 // 宣言行の 1 つ上（0-based）
        while (index >= 0) {
            val trimmed = fileLines.getOrNull(index)?.trim() ?: break
            when {
                trimmed.startsWith("context(") -> {
                    trimmed
                        .substringAfter("context(")
                        .substringBeforeLast(")")
                        .split(',')
                        .forEach { parameter ->
                            val type = parameter.substringAfter(':', parameter).trim()
                            if (type.isNotEmpty()) result += type
                        }
                    index--
                }

                trimmed.startsWith("@") -> index-- // アノテーション行はまたいで遡る

                else -> return result // KDoc の `*/`・空行・別宣言に到達したら終了
            }
        }
        return result
    }

/**
 * `ktFile` / `requiredKtFile` の block。1 ファイルの目録をここで完結して宣言する:
 * トップレベル宣言の許可（grant の列挙 = only 意味論）・[imports]・[maxLines]。
 * 全体の意味論は [manifest] を参照。
 *
 * grant 固有の規則だけここに書く:
 * - 単数形（[topLevelFunction] 等）は名前完全一致・必須・個数固定、
 *   複数形（[topLevelFunctions] 等）は名前パターンで任意個
 * - required / count は「その grant に一致した宣言の数」で検査。宣言順に意味はない
 * - 明示的な緩和（[topLevels] / [anyTopLevel]）を使う場所には理由コメントを書くこと
 */
@ManifestDslMarker
internal class KtFileBuilder internal constructor() {
    private val grants = mutableListOf<TopLevelGrant>()
    private var importAllowances: List<ImportAllowance>? = null
    private var maxLinesLimit: Int? = null

    /**
     * このファイルの import の許可を列挙する（deny by default）。列挙に一致しない import は違反。
     * 宣言なし・`imports { }`（空）= import ゼロのみ許可。1 つの ktFile に 1 回だけ宣言できる。
     */
    fun imports(block: ImportsBuilder.() -> Unit) {
        check(importAllowances == null) { "imports は 1 つの ktFile に 1 回だけ宣言する" }
        importAllowances = ImportsBuilder().apply(block).build()
    }

    /** このファイルの行数上限を上書きする（階層の [ManifestEntriesBuilder.maxLines] 既定より優先される）。 */
    fun maxLines(limit: Int) {
        require(limit > 0) { "maxLines は正の値で指定する: $limit" }
        maxLinesLimit = limit
    }

    internal fun buildImportAllowances(): List<ImportAllowance> = importAllowances.orEmpty()

    internal fun buildMaxLines(): Int? = maxLinesLimit

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

    /**
     * `fun <name>` をちょうど [count] 個（オーバーロード数まで固定）許可し、必須にする。
     * シグネチャ軸（[receiver] / [contextParameters] / [returns]）を指定すると、その形の関数にしか
     * 一致しない（[returns] 省略 body は `Unit` として照合。context は署名テキスト基準）。
     */
    fun topLevelFunction(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
        count: Int = 1,
        receiver: String? = null,
        contextParameters: Set<String>? = null,
        returns: String? = null,
    ) = exact(
        TopLevelKind.Function,
        name,
        visibility,
        count = count,
        receiver = receiver,
        contextParameters = contextParameters,
        returns = returns,
    )

    /** `val/var <name>` をちょうど [count] 個（拡張レシーバ違いまで固定）許可し、必須にする。 */
    fun topLevelProperty(
        name: String,
        visibility: TopLevelVisibility = TopLevelVisibility.Internal,
        count: Int = 1,
    ) = exact(TopLevelKind.Property, name, visibility, count = count)

    // --- 複数形: 名前パターン。既定は任意個・任意（required / count で締める）。
    //     一致範囲を絞る軸（nameStartsWith / nameEndsWith / visibility）を最低 1 つ指定すること —
    //     全省略は実質 anyTopLevel と同じ暗黙の全緩和になるため構築時に拒否する ---

    /** 名前パターン・シグネチャに一致するトップレベル関数を許可する。 */
    fun topLevelFunctions(
        nameStartsWith: String? = null,
        nameEndsWith: String? = null,
        visibility: TopLevelVisibility? = null,
        required: Boolean = false,
        count: Int? = null,
        receiver: String? = null,
        contextParameters: Set<String>? = null,
        returns: String? = null,
    ) = pattern(
        TopLevelKind.Function,
        nameStartsWith,
        nameEndsWith,
        visibility,
        required,
        count,
        receiver = receiver,
        contextParameters = contextParameters,
        returns = returns,
    )

    /**
     * 名前パターンに一致するトップレベルクラスを許可する。
     * [extends] を指定すると、そのクラスを（間接も含めて）継承しているクラスにしか一致しない。
     */
    fun topLevelClasses(
        nameStartsWith: String? = null,
        nameEndsWith: String? = null,
        visibility: TopLevelVisibility? = null,
        required: Boolean = false,
        count: Int? = null,
        extends: String? = null,
    ) = pattern(TopLevelKind.Class, nameStartsWith, nameEndsWith, visibility, required, count, extends = extends)

    // --- 明示的な緩和（使う場所には理由コメントを書くこと） ---

    fun topLevels(visibility: TopLevelVisibility = TopLevelVisibility.Private) {
        topLevels(setOf(visibility))
    }

    /** 指定した可視性のトップレベル宣言を種類・名前を問わず任意個許可する。 */
    fun topLevels(visibilities: Set<TopLevelVisibility>) {
        require(visibilities.isNotEmpty()) { "visibilities は空にできない" }
        require(visibilities != TopLevelVisibility.entries.toSet()) {
            "全可視性を許可するなら anyTopLevel() を明示的に使うこと（暗黙の全緩和を避ける）"
        }
        grants +=
            TopLevelGrant(
                description = "topLevels(visibilities = ${visibilities.joinToString("/") { it.name }})",
                required = false,
                count = null,
                matcher = visibilityIn(visibilities),
            )
    }

    /** すべてのトップレベル宣言を許可する（中身を問わない、の明示）。 */
    fun anyTopLevel() {
        grants +=
            TopLevelGrant(
                description = "anyTopLevel()",
                required = false,
                count = null,
                matcher = { true },
            )
    }

    internal fun buildGrants(): List<TopLevelGrant> = grants.toList()

    private fun exact(
        kind: TopLevelKind,
        name: String,
        visibility: TopLevelVisibility,
        count: Int,
        receiver: String? = null,
        contextParameters: Set<String>? = null,
        returns: String? = null,
    ) {
        require(count > 0) { "count は正の値で指定する: $count（$name）" }
        val signature =
            buildString {
                contextParameters?.let { append("context(${it.joinToString()}) ") }
                append("${visibility.keyword} ${kind.keyword} ")
                receiver?.let { append("$it.") }
                append(name)
                returns?.let { append(": $it") }
                if (count != 1) append(" ×$count")
            }
        grants +=
            TopLevelGrant(
                description = signature,
                required = true,
                count = count,
                matcher =
                    allOf(
                        kindIs(kind),
                        nameEqualsTo(name),
                        visibilityIn(setOf(visibility)),
                        receiver?.let(::receiverIs),
                        contextParameters?.let(::contextParametersAre),
                        returns?.let(::returnsType),
                    ),
            )
    }

    private fun pattern(
        kind: TopLevelKind,
        nameStartsWith: String?,
        nameEndsWith: String?,
        visibility: TopLevelVisibility?,
        required: Boolean,
        count: Int?,
        receiver: String? = null,
        contextParameters: Set<String>? = null,
        returns: String? = null,
        extends: String? = null,
    ) {
        require(count == null || count > 0) { "count は正の値で指定する: $count" }
        require(
            nameStartsWith != null || nameEndsWith != null || visibility != null ||
                receiver != null || contextParameters != null || returns != null || extends != null,
        ) {
            "パターン grant は一致範囲を絞る軸（nameStartsWith / nameEndsWith / visibility / receiver / " +
                "contextParameters / returns / extends）を最低 1 つ指定する — " +
                "すべての${kind.keyword}を許可したい場合は anyTopLevel() を明示的に使うこと"
        }
        val conditions =
            listOfNotNull(
                nameStartsWith?.let { "nameStartsWith = \"$it\"" },
                nameEndsWith?.let { "nameEndsWith = \"$it\"" },
                visibility?.let { "visibility = ${it.name}" },
                receiver?.let { "receiver = $it" },
                contextParameters?.let { "context(${it.joinToString()})" },
                returns?.let { "returns = $it" },
                extends?.let { "extends = $it" },
                if (required) "required" else null,
                count?.let { "count = $it" },
            )
        grants +=
            TopLevelGrant(
                description = "${kind.keyword}(${conditions.joinToString(", ")})",
                required = required,
                count = count,
                matcher =
                    allOf(
                        kindIs(kind),
                        nameStartsWith?.let(::namePrefixed),
                        nameEndsWith?.let(::nameSuffixed),
                        visibility?.let { visibilityIn(setOf(it)) },
                        receiver?.let(::receiverIs),
                        contextParameters?.let(::contextParametersAre),
                        returns?.let(::returnsType),
                        extends?.let(::extendsClass),
                    ),
            )
    }
}

/**
 * トップレベル宣言のみを返す。Konsist の `declarations()` は package 宣言と import 宣言も
 * 「宣言」として含むため、それらを除外する。
 */
internal fun KoFileDeclaration.topLevelDeclarations(): List<KoBaseDeclaration> =
    declarations(includeNested = false, includeLocal = false)
        .filterNot { it is KoPackageDeclaration || it is KoImportDeclaration }

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
