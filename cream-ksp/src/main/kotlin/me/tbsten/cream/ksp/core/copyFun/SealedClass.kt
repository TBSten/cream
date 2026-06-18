package me.tbsten.cream.ksp.core.copyFun

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.options.CreamOptions
import java.io.BufferedWriter

context(options: CreamOptions, logger: KSPLogger)
internal fun BufferedWriter.appendCopyToSealedClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation,
) {
    // A sealed target only ever produces copy functions whose receiver is the original [source]
    // (the annotated/source class threaded through the recursion), reaching every transitive
    // concrete leaf. Intermediate sealed nodes are never used as a receiver. Each leaf re-enters
    // appendCopyFunction, which derives notCopyToObject from [generateSourceAnnotation] itself, so
    // object leaves are skipped/kept correctly without threading a flag through.
    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            source,
            subclass,
            omitPackages,
            generateSourceAnnotation,
        )
    }
}
