package me.tbsten.cream.ksp.options

import me.tbsten.cream.ksp.InvalidCreamOptionException

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
            throw InvalidCreamOptionException(
                message = "Invalid ksp.arg[\"cream.copyFunNamingStrategy\"] = ${this["cream.copyFunNamingStrategy"]}." +
                        " It must be on of ${CopyFunNamingStrategy.entries.joinToString(", ")}",
                solution = "Set one of the following for ksp.arg: \n" +
                        CopyFunNamingStrategy.entries.joinToString("") { "- \"${it.name}\"\n" },
                cause = e,
            )
        },
        escapeDot = try {
            EscapeDot.valueOf(
                this["cream.escapeDot"] ?: EscapeDot.default.name
            )
        } catch (e: IllegalArgumentException) {
            throw InvalidCreamOptionException(
                message = "Invalid ksp.arg[\"cream.escapeDot\"] = ${this["cream.escapeDot"]}",
                solution = "Set one of the following for ksp.arg: \n" +
                        EscapeDot.entries.joinToString(", "),
                cause = e,
            )
        },
    )
}
