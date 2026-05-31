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

    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            targetClass,
            subclass,
            omitPackages,
            generateSourceAnnotation,
            options,
            notCopyToObject,
            visibility,
            funNameTemplate = funNameTemplate,
            generateTargetToSealedSubclasses = false,
        )
    }
}
