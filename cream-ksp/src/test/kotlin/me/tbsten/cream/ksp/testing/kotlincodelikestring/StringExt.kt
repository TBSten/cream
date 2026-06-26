package me.tbsten.cream.ksp.testing.kotlincodelikestring

internal fun escapeString(value: String): String =
    value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        .replace("$", "\\$")

internal fun escapeChar(value: Char): String =
    when (value) {
        '\\' -> "\\\\"
        '\'' -> "\\'"
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\t' -> "\\t"
        else -> value.toString()
    }

internal fun String.withPrefixEachLines(prefix: String) =
    this
        .lines()
        .joinToString("\n") { "$prefix$it" }

private val KOTLIN_HARD_KEYWORDS =
    setOf(
        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while",
    )

private val VALID_KOTLIN_IDENTIFIER = Regex("[A-Za-z_][A-Za-z0-9_]*")

/**
 * Kotlin の識別子としてそのまま使えない名前はバッククォートで囲んで返す。
 * 対象: hard keyword（`is` / `object` …）/ 識別子に使えない文字を含む（`under-package` のハイフンなど）/
 * 数字始まり / 空文字。enum 定数名や data class のプロパティ名をコードに埋め込むときに使う。
 */
internal fun String.escapeKotlinIdentifierIfNeeded(): String = if (matches(VALID_KOTLIN_IDENTIFIER) && this !in KOTLIN_HARD_KEYWORDS && !contains(Regex("\\s"))) this else "`$this`"
