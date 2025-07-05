package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import java.io.BufferedWriter

internal fun BufferedWriter.appendCopyFunction(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
    generateTargetToSealedSubclasses: Boolean = true,
) {
    when (target.classKind) {
        ClassKind.CLASS -> appendCopyToClassFunction(source, target, options)
        ClassKind.OBJECT -> appendCopyToObjectFunction(source, target, options)
        ClassKind.INTERFACE -> {
            if (target.isSealed())
                if (generateTargetToSealedSubclasses) {
                    appendCopyToSealedClassFunction(
                        source,
                        target,
                        options
                    )
                } else {
                    // no op
                }
            else throw InvalidCreamUsageException(
                message =
                    "Unsupported copy to ${
                        target.classKind.name.lowercase().replace("_", " ")
                    } (${target.fullName})." +
                            "It must be a sealed interface.",
                solution = "Please make ${target.fullName} a sealed interface.",
            )
        }

        else -> throw InvalidCreamUsageException(
            message =
                "Unsupported copy to ${
                    target.classKind.name.lowercase().replace("_", " ")
                } (${target.fullName}).",
            solution = "Please make ${target.fullName} a class or object or sealed interface.",
        )
    }
}
