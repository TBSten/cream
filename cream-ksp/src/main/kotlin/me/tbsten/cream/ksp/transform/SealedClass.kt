package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
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
    visibility: CopyVisibility = CopyVisibility.INHERIT,
    funNameTemplate: String = DefaultCopyFunctionName,
    logger: KSPLogger? = null,
) {
    // A sealed target only ever produces copy functions whose receiver is the original [source]
    // (the annotated/source class threaded through the recursion), reaching every transitive
    // concrete leaf. Intermediate sealed nodes are never used as a receiver.
    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            source,
            subclass,
            omitPackages,
            generateSourceAnnotation,
            options,
            notCopyToObject,
            visibility,
            funNameTemplate = funNameTemplate,
            logger = logger,
        )
    }
}
