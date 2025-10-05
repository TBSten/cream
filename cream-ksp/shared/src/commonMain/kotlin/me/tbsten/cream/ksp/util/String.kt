package me.tbsten.cream.ksp.util

import me.tbsten.cream.InternalCreamApi

@InternalCreamApi
fun lines(vararg lines: String, indent: String = ""): String =
    lines.joinToString("\n") { "$indent$it" }

@InternalCreamApi
fun StringBuilder.appendLines(vararg lines: String, indent: String = ""): String {
    val lines = lines(*lines, indent = indent)
    append(lines)
    return lines
}
