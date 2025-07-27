package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.options.CreamOptions
import java.io.BufferedWriter

internal fun BufferedWriter.appendCopyToSealedClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    omitPackages: List<String>,
    options: CreamOptions,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    notCopyToObject: Boolean,
) {
    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            source,
            subclass,
            omitPackages,
            generateSourceAnnotation,
            options,
            notCopyToObject,
        )
    }

    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            targetClass,
            subclass,
            omitPackages,
            generateSourceAnnotation,
            options,
            notCopyToObject,
            generateTargetToSealedSubclasses = false,
        )
    }
}
