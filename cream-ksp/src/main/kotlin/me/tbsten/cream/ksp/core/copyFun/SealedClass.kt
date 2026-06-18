package me.tbsten.cream.ksp.core.copyFun

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.options.CreamOptions
import java.io.BufferedWriter

context(options: CreamOptions, logger: KSPLogger)
internal fun BufferedWriter.appendCopyToSealedClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    notCopyToObject: Boolean,
    visibility: CopyVisibility = CopyVisibility.INHERIT,
    funNameTemplate: String = DefaultCopyFunctionName,
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
            notCopyToObject,
            visibility,
            funNameTemplate = funNameTemplate,
        )
    }
}
