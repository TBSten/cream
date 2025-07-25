package me.tbsten.cream.ksp.options


@Suppress("EnumEntryName")
internal enum class EscapeDot(
    val escape: (String) -> String,
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
    `backquote`({
        "`$it`"
    }),
    ;

    companion object {
        val default = `lower-camel-case`
    }
}
