package me.tbsten.cream.ksp.testing.kotlincodelikestring

fun buildKotlinCodeLikeString(
    value: Any?,
    indent: String = " ".repeat(4),
    className: (Any) -> String = { it.underPackageClassName },
    fallback: Fallback = Fallback.throwError(),
    transform: Transform = Transform { null },
): String =
    transform(value)
        ?: transformBuiltIns(value, indent, className, fallback, transform)
        ?: fallback(value)
