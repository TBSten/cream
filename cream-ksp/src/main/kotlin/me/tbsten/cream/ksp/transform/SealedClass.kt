package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.options.CreamOptions
import java.io.BufferedWriter

internal fun BufferedWriter.appendCopyToSealedClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    options: CreamOptions,
    notCopyToObject: Boolean,
) {
    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            source,
            subclass,
            options,
            notCopyToObject,
        )
    }

    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            targetClass,
            subclass,
            options,
            notCopyToObject,
            generateTargetToSealedSubclasses = false,
        )
    }
}
