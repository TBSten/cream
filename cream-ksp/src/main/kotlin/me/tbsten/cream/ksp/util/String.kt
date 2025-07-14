package me.tbsten.cream.ksp.util

internal fun lines(vararg lines: String, indent: String = ""): String =
    lines.joinToString("\n") { "$indent$it" }
