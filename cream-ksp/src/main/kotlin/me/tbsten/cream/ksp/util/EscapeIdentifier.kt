package me.tbsten.cream.ksp.util

/**
 * Kotlin hard keywords. These can never be used as a bare identifier and must be backquoted
 * when they appear as a property / parameter name in generated code.
 *
 * This is the complete set of hard keywords from the Kotlin reference:
 * https://kotlinlang.org/docs/reference/keyword-reference.html ("Hard keywords"). The
 * operator-spelled variants listed there (`as?`, `!in`, `!is`) are omitted because they are not
 * identifier strings a property / parameter could be named, so they never reach this check.
 *
 * Soft / modifier keywords (e.g. `data`, `out`, `sealed`) are intentionally NOT listed: they are
 * valid bare identifiers in identifier position, so backquoting them is unnecessary (a redundant
 * backquote would also be valid, but we keep output minimal).
 */
private val kotlinHardKeywords =
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

/**
 * A name is a valid bare (unescaped) identifier when it is non-empty, starts with a Unicode letter
 * or underscore, and otherwise contains only Unicode letters, decimal digits, and underscores. The
 * Unicode-aware classes matter: Kotlin allows non-ASCII identifiers (e.g. Japanese `税込金額`), so an
 * ASCII-only check would wrongly backquote them and drift the generated output.
 *
 * This deliberately ignores characters that are illegal even inside backquotes (`. ; [ ] / < > : \`
 * and friends): such names cannot be represented in Kotlin at all, so cream cannot rescue them by
 * escaping — they are out of scope (see issue #94). For the names cream can rescue (keywords and
 * whitespace-containing names), [escapeKotlinIdentifier] wraps them in backquotes.
 */
private val bareIdentifierRegex = Regex("[\\p{L}_][\\p{L}\\p{Nd}_]*")

/**
 * Wrap this identifier in backquotes when it cannot appear as a bare Kotlin identifier — i.e. it is
 * a [hard keyword][kotlinHardKeywords] or is not a [bare identifier][bareIdentifierRegex] (for
 * example it contains a space). Already-valid bare identifiers are returned unchanged so generated
 * code stays readable.
 *
 * Used at every site where cream interpolates a source property / target constructor-parameter name
 * into generated code (parameter declarations, `this.<name>` defaults, and `name = name` named
 * arguments). Without it, a property named after a keyword (e.g. `in`) or with a space produces a
 * syntax error in the generated source.
 */
internal fun String.escapeKotlinIdentifier(): String =
    if (this in kotlinHardKeywords || !bareIdentifierRegex.matches(this)) {
        "`$this`"
    } else {
        this
    }
