package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import java.io.BufferedWriter

internal fun BufferedWriter.appendCopyFunction(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    options: CreamOptions,
) {
    when (target.classKind) {
        ClassKind.CLASS -> appendCopyToClassFunction(source, target, options)
        ClassKind.OBJECT -> appendCopyToObjectFunction(source, target, options)
        ClassKind.INTERFACE -> {
            if (target.isSealed()) appendCopyToSealedClassFunction(source, target, options)
            else error(
                "Unsupported copy target class kind: ${target.classKind} (${target.fullName}). " +
                        "It must be a sealed interface."
            )
        }

        else -> error(
            "Unsupported copy target class kind: ${target.classKind} (${target.fullName})"
        )
    }
}
