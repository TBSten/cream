package me.tbsten.cream.ksp.options

internal data class CreamOptions(
    val copyFunNamePrefix: String,
    val copyFunNamingStrategy: CopyFunNamingStrategy,
    val escapeDot: EscapeDot,
)

internal fun Map<String, String>.toCreamOptions(): CreamOptions {
    return CreamOptions(
        copyFunNamePrefix = this["cream.copyFunNamePrefix"] ?: "copyTo",
        copyFunNamingStrategy = try {
            CopyFunNamingStrategy.valueOf(
                this["cream.copyFunNamingStrategy"] ?: CopyFunNamingStrategy.default.name,
            )
        } catch (e: IllegalArgumentException) {
            error(
                "Invalid ksp.arg[\"cream.copyFunNamingStrategy\"] = ${this["cream.copyFunNamingStrategy"]}. It must be on of ${
                    CopyFunNamingStrategy.entries.joinToString(", ")
                }",
                /* TODO cause = e, */
            )
        },
        escapeDot = try {
            EscapeDot.valueOf(
                this["cream.escapeDot"] ?: EscapeDot.default.name
            )
        } catch (e: IllegalArgumentException) {
            error(
                "Invalid ksp.arg[\"cream.escapeDot\"] = ${this["cream.escapeDot"]}. It must be on of ${
                    EscapeDot.entries.joinToString(", ")
                }",
                /* TODO cause = e, */
            )
        },
    )
}
