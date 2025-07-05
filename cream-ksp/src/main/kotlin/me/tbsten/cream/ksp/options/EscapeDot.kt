package me.tbsten.cream.ksp.options


@Suppress("EnumEntryName")
internal enum class EscapeDot(
    val escape: (String) -> String,
) {
    `replace-to-underscore`(escapeString = "_"),
    `pascal-case`({ it.split(".").joinToString("") { it.replaceFirstChar { it.uppercase() } } }),
    ;

    constructor(escapeString: String) : this({ it.replace(".", escapeString) })

    companion object {
        val default = `replace-to-underscore`
    }
}
