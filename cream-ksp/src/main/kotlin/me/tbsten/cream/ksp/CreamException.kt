package me.tbsten.cream.ksp

import me.tbsten.cream.ksp.util.lines

internal abstract class CreamException(
    message: String,
    solution: String? = null,
    cause: Throwable? = null,
) : Exception(
    buildString {
        appendLine(message)

        if (solution != null) {
            appendLine()
            appendLine("Solution: ")
            solution.lineSequence().forEach {
                appendLine("  $it")
            }
        }
    },
    cause,
)

internal open class InvalidCreamUsageException(
    message: String,
    solution: String?,
    cause: Throwable? = null,
) : CreamException(
    message = "Invalid cream usage: $message",
    solution = solution,
    cause = cause,
)

internal class InvalidCreamOptionException(
    message: String,
    solution: String?,
    cause: Throwable? = null,
) : InvalidCreamUsageException(
    message = "Invalid option: $message",
    solution = solution,
    cause = cause,
)

internal class UnknownCreamException(
    message: String? = null,
    solution: String? = null,
    cause: Throwable? = null,
) : CreamException(
    message = ("Unexpected error" + message?.let { ": $it" }),
    solution = solution
        ?: lines(
            "Please report this issue at:",
            "",
            "    https://github.com/TBSten/cream/issues",
            "",
        ),
    cause = cause,
)
