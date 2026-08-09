package me.tbsten.cream.ksp.options

import kotlinx.serialization.Serializable
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.InternalCreamApi
import me.tbsten.cream.ksp.InvalidCreamOptionException
import me.tbsten.cream.ksp.util.lines
import kotlin.reflect.KProperty1

@InternalCreamApi
@Serializable
public data class CreamOptions(
    val copyFunNamePrefix: String,
    val copyFunNamingStrategy: CopyFunNamingStrategy,
    val escapeDot: EscapeDot,
    val notCopyToObject: Boolean,
    val defaultVisibility: CopyVisibility,
    val autoValueClassMapping: Boolean,
) {
    public companion object {
        public val default: CreamOptions =
            CreamOptions(
                copyFunNamePrefix = "copyTo",
                copyFunNamingStrategy = CopyFunNamingStrategy.default,
                escapeDot = EscapeDot.default,
                notCopyToObject = false,
                defaultVisibility = CopyVisibility.INHERIT,
                autoValueClassMapping = true,
            )

        public val properties: List<KProperty1<CreamOptions, *>> =
            listOf(
                CreamOptions::copyFunNamePrefix,
                CreamOptions::copyFunNamingStrategy,
                CreamOptions::escapeDot,
                CreamOptions::notCopyToObject,
                CreamOptions::defaultVisibility,
                CreamOptions::autoValueClassMapping,
            )
    }
}

@InternalCreamApi
public fun Map<String, String>.toCreamOptions(): CreamOptions =
    CreamOptions(
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
                    this["cream.escapeDot"] ?: CreamOptions.default.escapeDot.name,
                )
            } catch (e: IllegalArgumentException) {
                invalidEscapeDotError(
                    actualValue = this["cream.escapeDot"],
                    cause = e,
                )
            },
        notCopyToObject =
            this["cream.notCopyToObject"]?.lowercase() == "true",
        defaultVisibility =
            this["cream.defaultVisibility"]?.let { rawValue ->
                try {
                    // Accept the CopyVisibility enum names case-insensitively, so both
                    // "INTERNAL" (the serialized form) and "internal" are valid.
                    CopyVisibility.valueOf(rawValue.uppercase())
                } catch (e: IllegalArgumentException) {
                    invalidDefaultVisibilityError(
                        actualValue = rawValue,
                        cause = e,
                    )
                }
            } ?: CreamOptions.default.defaultVisibility,
        // Default true (issue #21): only an explicit "false" disables the automatic value class
        // mapping, mirroring notCopyToObject's lenient boolean parsing (no invalid-value error).
        autoValueClassMapping =
            this["cream.autoValueClassMapping"]?.lowercase() != "false",
    )

@OptIn(InternalCreamApi::class)
@Suppress("NOTHING_TO_INLINE")
private inline fun invalidCopyFunNamingStrategyError(
    actualValue: String?,
    cause: IllegalArgumentException,
): Nothing =
    throw InvalidCreamOptionException(
        message =
            lines(
                "Invalid ksp.arg[\"cream.copyFunNamingStrategy\"] = $actualValue.",
                "It must be on of ${CopyFunNamingStrategy.entries.joinToString(", ")}",
            ),
        solution =
            lines(
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
        solution =
            lines(
                "Set one of the following for ksp.arg:",
                "",
                *EscapeDot.entries
                    .map { "  - $it" }
                    .toTypedArray(),
                "",
            ),
        cause = cause,
    )

@Suppress("NOTHING_TO_INLINE")
@OptIn(InternalCreamApi::class)
private inline fun invalidDefaultVisibilityError(
    actualValue: String?,
    cause: IllegalArgumentException,
): Nothing =
    throw InvalidCreamOptionException(
        message =
            lines(
                "Invalid ksp.arg[\"cream.defaultVisibility\"] = $actualValue.",
                "It must be one of ${CopyVisibility.entries.joinToString(", ") { it.name }}",
            ),
        solution =
            lines(
                "Set one of the following for ksp.arg:",
                "",
                *CopyVisibility.entries
                    .map { "  - \"${it.name}\"" }
                    .toTypedArray(),
                "",
            ),
        cause = cause,
    )
