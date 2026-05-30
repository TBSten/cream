package me.tbsten.cream.ksp.process

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendSealedCopyFunction
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.underPackageName

internal fun CreamSymbolProcessor.processSealedCopy(resolver: Resolver): List<KSAnnotated> {
    val (sealedCopyTargets, invalidSealedCopyTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = SealedCopy::class.fullName,
            ).partition { it.validate() }

    sealedCopyTargets.forEach { annotated ->
        if (annotated !is KSClassDeclaration) {
            throw InvalidCreamUsageException(
                message = "@${SealedCopy::class.simpleName} must be applied to a sealed class/interface.",
                solution = "Please apply @${SealedCopy::class.simpleName} to a `sealed class` or `sealed interface`.",
            )
        }
        if (!annotated.isSealed()) {
            throw InvalidCreamUsageException(
                message =
                    "@${SealedCopy::class.simpleName} must be applied to a sealed class/interface, " +
                        "but ${annotated.fullName} is not sealed.",
                solution = "Make ${annotated.fullName} a `sealed class` or `sealed interface`.",
            )
        }

        // Read directly from KSAnnotation rather than via getAnnotationsByType:
        // the typed proxy throws NoSuchElementException on AA-backed KSP2 when
        // accessing a field that wasn't given explicitly (the bare `@SealedCopy`
        // case). The raw arguments list correctly omits absent values, letting us
        // fall back to the documented defaults.
        val sealedAnnotations =
            annotated.annotations
                .filter {
                    it.annotationType
                        .resolve()
                        .declaration.fullName == SealedCopy::class.qualifiedName
                }.toList()
        if (sealedAnnotations.isEmpty()) {
            // Defensive: getSymbolsWithAnnotation matched but no SealedCopy
            // annotation could be resolved (e.g. broken classpath entry).
            return@forEach
        }

        codeGenerator
            .createNewKotlinFile(
                dependencies =
                    Dependencies(
                        aggregating = true,
                        annotated.containingFile!!,
                    ),
                packageName = annotated.packageName,
                fileName = "SealedCopy__${annotated.underPackageName}",
            ) {
                it.appendLine("import me.tbsten.cream.*")
                it.appendLine()

                sealedAnnotations.forEach { sealedAnnotation ->
                    val funName =
                        sealedAnnotation.arguments
                            .firstOrNull { it.name?.asString() == "funName" }
                            ?.value as? String
                            ?: "copy"
                    val nonCopyableStrategy =
                        sealedAnnotation.arguments
                            .firstOrNull { it.name?.asString() == "nonCopyableStrategy" }
                            ?.value
                            ?.toNonCopyableStrategy()
                            ?: NonCopyableStrategy.ERROR

                    val (kdocDescription, kdocExamples) = sealedAnnotation.extractKDoc()

                    it.appendSealedCopyFunction(
                        sealedClass = annotated,
                        funName = funName,
                        nonCopyableStrategy = nonCopyableStrategy,
                        omitPackages =
                            listOf(
                                "kotlin",
                                annotated.packageName.asString(),
                            ),
                        generateSourceAnnotation =
                            GenerateSourceAnnotation.SealedCopy(
                                annotationTarget = annotated,
                                kdocDescription = kdocDescription,
                                kdocExamples = kdocExamples,
                            ),
                    )
                }
            }
    }

    return invalidSealedCopyTargets
}

/**
 * Read an annotation argument's value (as returned by KSP — typically a `KSType` or
 * `KSClassDeclaration` for enum entries, but [Enum] / [String] are also accepted as a
 * defensive fallback) and resolve it to a [NonCopyableStrategy] entry. Returns `null`
 * when the value's simple name does not match any entry.
 */
private fun Any.toNonCopyableStrategy(): NonCopyableStrategy? {
    val entryName =
        when (this) {
            is KSClassDeclaration -> simpleName.asString()
            is KSType -> declaration.simpleName.asString()
            is Enum<*> -> name
            is String -> this
            else -> return null
        }
    return runCatching { NonCopyableStrategy.valueOf(entryName) }.getOrNull()
}
