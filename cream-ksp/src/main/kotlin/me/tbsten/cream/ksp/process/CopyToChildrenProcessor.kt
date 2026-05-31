package me.tbsten.cream.ksp.process

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.util.annotationsOf
import me.tbsten.cream.ksp.util.classListArgument
import me.tbsten.cream.ksp.util.copyVisibilityArgument
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.extractPropertyMappings
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.requireClassDeclaration
import me.tbsten.cream.ksp.util.resolveToClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName

internal fun CreamSymbolProcessor.processCopyToChildren(resolver: Resolver): List<KSAnnotated> {
    val (copyToChildrenTargets, invalidCopyToChildrenTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyToChildren::class.fullName,
            ).partition { it.validate() }

    copyToChildrenTargets.forEach { copyToChildren ->
        val sourceSealedClass =
            run {
                if (copyToChildren !is KSClassDeclaration) {
                    throw InvalidCreamUsageException(
                        message =
                            "@${CopyToChildren::class.simpleName} annotation must be applied to a sealed class/interface." +
                                if (copyToChildren is KSDeclaration) {
                                    copyToChildren.simpleName.asString() +
                                        " is not sealed class/interface"
                                } else {
                                    ""
                                },
                        solution = (copyToChildren as? KSDeclaration)?.let { "Make ${it.fullName} a sealed class/interface." },
                    )
                }

                if (!copyToChildren.isSealed()) {
                    // Avoid `fullName`, which throws UnknownCreamException when qualifiedName is null
                    // (e.g. local/anonymous declarations) and would mask this InvalidCreamUsageException.
                    val displayName =
                        copyToChildren.qualifiedName?.asString()
                            ?: copyToChildren.simpleName.asString()
                    throw InvalidCreamUsageException(
                        message =
                            "@${CopyToChildren::class.simpleName} annotation must be applied to a sealed class/interface, " +
                                "but $displayName is not sealed (classKind: ${copyToChildren.classKind}).",
                        solution = "Make $displayName a sealed class/interface.",
                    )
                }

                copyToChildren
            }
        // Enclose notCopyToObject in runCatching because it may cause an error if notCopyToObject cannot be obtained.
        val notCopyToObject =
            runCatching {
                val copyToChildrenAnnotation =
                    sourceSealedClass.getAnnotationsByType(CopyToChildren::class).firstOrNull()
                copyToChildrenAnnotation
                    ?.notCopyToObject
                    ?: options.notCopyToObject
            }.getOrDefault(false)

        val copyToChildrenAnnotation =
            sourceSealedClass.annotationsOf(CopyToChildren::class).firstOrNull()

        val (kdocDescription, kdocExamples) =
            copyToChildrenAnnotation
                ?.extractKDoc()
                ?: ("" to emptyList())

        val visibility =
            copyToChildrenAnnotation?.copyVisibilityArgument() ?: CopyVisibility.INHERIT

        val targetClasses = sourceSealedClass.getSealedSubclasses()

        // Warn for @CopyToChildren.Exclude on non-abstract properties (no-op: not in generated copy functions)
        sourceSealedClass
            .getAllProperties()
            .filter { !it.isAbstract() && it.annotationsOf(CopyToChildren.Exclude::class).any() }
            .forEach { prop ->
                logger.warn(
                    "@Exclude on '${prop.simpleName.asString()}' has no effect: not a matched property",
                    prop,
                )
            }

        codeGenerator
            .createNewKotlinFile(
                dependencies =
                    Dependencies(
                        aggregating = true,
                        sourceSealedClass.containingFile!!,
                    ),
                packageName = sourceSealedClass.packageName,
                fileName = "CopyToChildren__${sourceSealedClass.underPackageName}",
            ) {
                it.appendLine("import me.tbsten.cream.*")
                it.appendLine()

                targetClasses.forEach { targetClass ->
                    // generate sourceClass to sourceClass copy function
                    it.appendCopyFunction(
                        source = sourceSealedClass,
                        target = targetClass,
                        omitPackages =
                            listOf(
                                "kotlin",
                                sourceSealedClass.packageName.asString(),
                            ),
                        options = options,
                        generateSourceAnnotation =
                            GenerateSourceAnnotation.CopyToChildren(
                                annotationTarget = sourceSealedClass,
                                kdocDescription = kdocDescription,
                                kdocExamples = kdocExamples,
                            ),
                        notCopyToObject = notCopyToObject,
                        visibility = visibility,
                        logger = logger,
                    )
                }
            }
    }

    return invalidCopyToChildrenTargets
}
