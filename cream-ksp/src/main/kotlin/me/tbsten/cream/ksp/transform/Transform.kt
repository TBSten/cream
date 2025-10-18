package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import java.io.BufferedWriter

internal fun BufferedWriter.appendCopyFunction(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    options: CreamOptions,
    notCopyToObject: Boolean,
    generateTargetToSealedSubclasses: Boolean = true,
) {
    when (target.classKind) {
        ClassKind.CLASS ->
            appendCopyToClassFunction(
                source,
                target,
                generateSourceAnnotation,
                omitPackages,
                options,
            )

        ClassKind.OBJECT ->
            if (!notCopyToObject)
                appendCopyToObjectFunction(source, target, generateSourceAnnotation, options)

        ClassKind.INTERFACE -> {
            if (target.isSealed())
                if (generateTargetToSealedSubclasses) {
                    appendCopyToSealedClassFunction(
                        source,
                        target,
                        omitPackages,
                        options,
                        generateSourceAnnotation,
                        notCopyToObject,
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

internal fun BufferedWriter.appendCombineToFunction(
    primarySource: KSClassDeclaration,
    otherSources: List<KSClassDeclaration>,
    target: KSClassDeclaration,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation<*>,
    options: CreamOptions,
) {
    when (target.classKind) {
        ClassKind.CLASS,
        ClassKind.ANNOTATION_CLASS,
            ->
            appendCombineToClassFunction(
                primarySource,
                otherSources,
                target,
                generateSourceAnnotation,
                omitPackages,
                options,
            )

        ClassKind.OBJECT ->
            if (!options.notCopyToObject)
                appendCombineToObjectFunction(
                    primarySource,
                    otherSources,
                    target,
                    generateSourceAnnotation,
                    options,
                )

        else -> throw InvalidCreamUsageException(
            message =
                "Unsupported combine to ${
                    target.classKind.name.lowercase().replace("_", " ")
                } (${target.fullName}).",
            solution = "Please make ${target.fullName} a class or object.",
        )
    }
}
