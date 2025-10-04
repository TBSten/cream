package me.tbsten.cream.ksp.options

import me.tbsten.cream.InternalCreamApi
import me.tbsten.cream.ksp.InvalidCreamOptionException
import me.tbsten.cream.ksp.util.lines

@InternalCreamApi
data class CreamOptions(
    val copyFunNamePrefix: String,
    val copyFunNamingStrategy: CopyFunNamingStrategy,
    val escapeDot: EscapeDot,
    val notCopyToObject: Boolean,
) {
    companion object {
        val default = CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.default,
            escapeDot = EscapeDot.default,
            notCopyToObject = false,
        )
    }
}

@InternalCreamApi
fun Map<String, String>.toCreamOptions(): CreamOptions {
    return CreamOptions(
        copyFunNamePrefix =
            this["cream.copyFunNamePrefix"] ?: CreamOptions.default.copyFunNamePrefix,
        copyFunNamingStrategy =
            try {
                CopyFunNamingStrategy.valueOf(
                    this["cream.copyFunNamingStrategy"] ?: CreamOptions.default.copyFunNamingStrategy.name,
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
                    this["cream.escapeDot"] ?: CreamOptions.default.escapeDot.name
                )
            } catch (e: IllegalArgumentException) {
                invalidEscapeDotError(
                    actualValue = this["cream.escapeDot"],
                    cause = e,
                )
            },
        notCopyToObject =
            this["cream.notCopyToObject"]?.lowercase() == "true"
    )
}

@OptIn(InternalCreamApi::class)
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
@OptIn(InternalCreamApi::class)
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
