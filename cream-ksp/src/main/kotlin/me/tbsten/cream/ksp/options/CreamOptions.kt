package me.tbsten.cream.ksp.options

import me.tbsten.cream.ksp.InvalidCreamOptionException
import me.tbsten.cream.ksp.util.lines

internal data class CreamOptions(
    val copyFunNamePrefix: String,
    val copyFunNamingStrategy: CopyFunNamingStrategy,
    val escapeDot: EscapeDot,
)

internal fun Map<String, String>.toCreamOptions(): CreamOptions {
    return CreamOptions(
        copyFunNamePrefix =
            this["cream.copyFunNamePrefix"] ?: "copyTo",
        copyFunNamingStrategy =
            try {
                CopyFunNamingStrategy.valueOf(
                    this["cream.copyFunNamingStrategy"] ?: CopyFunNamingStrategy.default.name,
                )
            } catch (e: IllegalArgumentException) {
                invalidCopyFunNamingStrategyError(
                    actualValue = this["cream.copyFunNamingStrategy"],
                    cause = e,
                )
            },
        escapeDot =
            try {
                EscapeDot.valueOf(
                    this["cream.escapeDot"] ?: EscapeDot.default.name
                )
            } catch (e: IllegalArgumentException) {
                invalidEscapeDotError(
                    actualValue = this["cream.escapeDot"],
                    cause = e,
                )
            },
    )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun invalidCopyFunNamingStrategyError(
    actualValue: String?,
    cause: IllegalArgumentException,
): Nothing =
    throw InvalidCreamOptionException(
        message = lines(
            "Invalid ksp.arg[\"cream.copyFunNamingStrategy\"] = ${actualValue}.",
            "It must be on of ${CopyFunNamingStrategy.entries.joinToString(", ")}",
        ),
        solution = lines(
            "Set one of the following for ksp.arg: \n",
            "",
            *CopyFunNamingStrategy.entries
                .map { "  - \"${it.name}\"" }
                .toTypedArray(),
            "",
        ),
        cause = cause,
    )

@Suppress("NOTHING_TO_INLINE")
private inline fun invalidEscapeDotError(
    actualValue: String?,
    cause: IllegalArgumentException,
): Nothing =
    throw InvalidCreamOptionException(
        message = "Invalid ksp.arg[\"cream.escapeDot\"] = $actualValue",
        solution = lines(
            "Set one of the following for ksp.arg:",
            "",
            *EscapeDot.entries
                .map { "  - $it" }
                .toTypedArray(),
            "",
        ),
        cause = cause,
    )
