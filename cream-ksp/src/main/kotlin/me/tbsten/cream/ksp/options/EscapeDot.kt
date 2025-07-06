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
    `replace-to-underscore`(escapeString = "_"),
    ;

    constructor(escapeString: String) : this({ it.replace(".", escapeString) })

    companion object {
        val default = `lower-camel-case`
    }
}
