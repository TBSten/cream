package me.tbsten.cream.ksp.util

internal fun lines(vararg lines: String, indent: String = ""): String =
    lines.joinToString("\n") { "$indent$it" }

internal fun StringBuilder.appendLines(vararg lines: String, indent: String = ""): String {
    val lines = lines(*lines, indent = indent)
    append(lines)
    return lines
}
