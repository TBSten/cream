package me.tbsten.cream.ksp.options

import me.tbsten.cream.InternalCreamApi


@Suppress("EnumEntryName")
@InternalCreamApi
public enum class EscapeDot(
    public val escape: (String) -> String,
) {
    `lower-camel-case`({
        it
            .split(".")
            .joinToString("") { it.replaceFirstChar { it.uppercase() } }
            .replaceFirstChar { it.lowercase() }
    }),
    `replace-to-underscore`({
        ("_" + it.replace(".", "_"))
            .replace(Regex("_+"), "_")
    }),
    ;

    public companion object {
        public val default: EscapeDot = `lower-camel-case`
    }
}
