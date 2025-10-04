package me.tbsten.cream.ksp

import me.tbsten.cream.InternalCreamApi
import me.tbsten.cream.ksp.util.appendLines

@InternalCreamApi
abstract class CreamException(
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

@InternalCreamApi
open class InvalidCreamUsageException(
    message: String,
    solution: String?,
    cause: Throwable? = null,
) : CreamException(
    message = "Invalid cream usage: $message",
    solution = solution,
    cause = cause,
)

@InternalCreamApi
class InvalidCreamOptionException(
    message: String,
    solution: String?,
    cause: Throwable? = null,
) : InvalidCreamUsageException(
    message = "Invalid option: $message",
    solution = solution,
    cause = cause,
)

@InternalCreamApi
class UnknownCreamException(
    message: String? = null,
    solution: String? = null,
    cause: Throwable? = null,
) : CreamException(
    message = ("Unexpected error" + message?.let { ": $it" }),
    solution = solution ?: reportToGithub(),
    cause = cause,
)

@InternalCreamApi
fun reportToGithub(vararg with: String) =
    buildString {
        appendLines(
            "Please report this issue at:",
            "",
            "    https://github.com/TBSten/cream/issues",
            "",
        )

        if (with.isNotEmpty()) {
            appendLines("  and report problems with:")

            with.forEach {
                appendLines("    - $it")
            }

            appendLine()
        }
    }
