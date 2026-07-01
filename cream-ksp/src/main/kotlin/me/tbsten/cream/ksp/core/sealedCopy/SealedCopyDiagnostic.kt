package me.tbsten.cream.ksp.core.sealedCopy

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.core.common.underPackageName

internal fun nonCopyableErrorException(
    sealedClass: KSClassDeclaration,
    nonCopyableLeaves: List<SealedCopyLeaf.NonCopyable>,
    funName: String,
): InvalidCreamUsageException {
    val objectLeaves = nonCopyableLeaves.filter { it.declaration.classKind == ClassKind.OBJECT }
    val classLeaves = nonCopyableLeaves.filterNot { it.declaration.classKind == ClassKind.OBJECT }

    val sealedName = sealedClass.underPackageName
    val message =
        buildString {
            append("Cannot generate $funName() for sealed type '$sealedName' because ")
            if (objectLeaves.isNotEmpty() && classLeaves.isEmpty()) {
                append(
                    "it contains object subtype(s): " +
                        objectLeaves.joinToString { it.declaration.underPackageName },
                )
                append(". Objects are singletons and have no .copy() to delegate to.")
            } else if (objectLeaves.isEmpty() && classLeaves.isNotEmpty()) {
                append(
                    "the following subclass(es) have no compatible 'copy(...)' function: " +
                        classLeaves.joinToString { it.declaration.underPackageName },
                )
            } else {
                append(
                    "the following subtype(s) cannot be copied: " +
                        nonCopyableLeaves.joinToString { it.declaration.underPackageName },
                )
            }
        }

    val solution =
        buildString {
            appendLine("Choose one of the following strategies on @SealedCopy:")
            appendLine("  • @SealedCopy(nonCopyableStrategy = RETURN_AS_IS)")
            appendLine("    → emits 'is X -> this' for non-copyable branches")
            appendLine("  • @SealedCopy(nonCopyableStrategy = RETURN_NULL)")
            appendLine("    → widens the return type to '$sealedName?' and emits 'is X -> null'")
            if (classLeaves.isNotEmpty()) {
                appendLine()
                appendLine("For non-data class subtypes you can also:")
                appendLine("  • Make the subtype a 'data class'")
                appendLine("  • Add a 'copy(...)' member function that accepts the abstract properties")
                appendLine("  • Or annotate that copy-shaped function with @SealedCopy.Via")
            }
        }

    return InvalidCreamUsageException(message = message, solution = solution)
}
