package me.tbsten.cream.ksp.process

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyMapping
import me.tbsten.cream.ksp.CreamSymbolProcessor
import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.InvalidCreamUsageException
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.util.createNewKotlinFile
import me.tbsten.cream.ksp.util.extractKDoc
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.requireClassDeclaration
import me.tbsten.cream.ksp.util.resolveToClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName

/**
 * Data class to hold CopyMapping annotation information
 *
 * @property sourceClass The source class for the mapping
 * @property targetClass The target class for the mapping
 * @property canReverse Whether bidirectional mapping is enabled
 * @property propertyMappings List of property name mappings (source property name -> target property name)
 * @property kdocDescription User-provided KDoc description (from `kdoc.description`)
 * @property kdocExamples User-provided KDoc examples (from `kdoc.examples`)
 */
private data class CopyMappingInfo(
    val sourceClass: KSClassDeclaration,
    val targetClass: KSClassDeclaration,
    val canReverse: Boolean,
    val propertyMappings: List<Pair<String, String>>,
    val kdocDescription: String,
    val kdocExamples: List<String>,
)

internal fun CreamSymbolProcessor.processCopyMapping(resolver: Resolver): List<KSAnnotated> {
    val (copyMappingTargets, invalidCopyMappingTargets) =
        resolver
            .getSymbolsWithAnnotation(
                annotationName = CopyMapping::class.fullName,
            ).partition { it.validate() }

    copyMappingTargets.forEach { target ->
        val annotatedDeclaration =
            (target as? KSClassDeclaration)
                ?: throw InvalidCreamUsageException(
                    message = "@${CopyMapping::class.simpleName} must be applied to a class.",
                    solution = "Please apply @${CopyMapping::class.simpleName} to a `class` or `object`",
                )

        // Extract all CopyMapping annotations from the target
        val copyMappings =
            target
                .annotations
                .filter {
                    it.annotationType
                        .resolve()
                        .declaration.fullName == CopyMapping::class.qualifiedName
                }.map { annotation ->
                    val sourceType =
                        annotation.arguments
                            .firstOrNull { it.name?.asString() == "source" }
                            ?.value as? KSType
                            ?: throw InvalidCreamUsageException(
                                message = "source parameter is required in @${CopyMapping::class.simpleName}",
                                solution = "Specify source class in @${CopyMapping::class.simpleName}",
                            )

                    val targetType =
                        annotation.arguments
                            .firstOrNull { it.name?.asString() == "target" }
                            ?.value as? KSType
                            ?: throw InvalidCreamUsageException(
                                message = "target parameter is required in @${CopyMapping::class.simpleName}",
                                solution = "Specify target class in @${CopyMapping::class.simpleName}",
                            )

                    val canReverse =
                        annotation.arguments
                            .firstOrNull { it.name?.asString() == "canReverse" }
                            ?.value as? Boolean
                            ?: false

                    val propertyMappings =
                        annotation.arguments
                            .firstOrNull { it.name?.asString() == "properties" }
                            ?.value as? List<*>
                            ?: emptyList<Any>()

                    val propertyMaps =
                        propertyMappings.mapNotNull { mapping ->
                            val mapAnnotation = mapping as? KSAnnotation ?: return@mapNotNull null
                            val sourceProperty =
                                mapAnnotation.arguments
                                    .firstOrNull { it.name?.asString() == "source" }
                                    ?.value as? String
                            val targetProperty =
                                mapAnnotation.arguments
                                    .firstOrNull { it.name?.asString() == "target" }
                                    ?.value as? String

                            if (sourceProperty != null && targetProperty != null) {
                                sourceProperty to targetProperty
                            } else {
                                null
                            }
                        }

                    val sourceClass =
                        sourceType.declaration as? KSClassDeclaration
                            ?: throw InvalidCreamUsageException(
                                message = "${sourceType.declaration.fullName} (Specified in @${CopyMapping::class.simpleName}.source) must be a class.",
                                solution = "Specify a class in @${CopyMapping::class.simpleName}.source",
                            )

                    val targetClass =
                        targetType.declaration as? KSClassDeclaration
                            ?: throw InvalidCreamUsageException(
                                message = "${targetType.declaration.fullName} (Specified in @${CopyMapping::class.simpleName}.target) must be a class.",
                                solution = "Specify a class in @${CopyMapping::class.simpleName}.target",
                            )

                    val (kdocDescription, kdocExamples) = annotation.extractKDoc()

                    CopyMappingInfo(
                        sourceClass = sourceClass,
                        targetClass = targetClass,
                        canReverse = canReverse,
                        propertyMappings = propertyMaps,
                        kdocDescription = kdocDescription,
                        kdocExamples = kdocExamples,
                    )
                }

        // Group by source class to create one file per source package
        copyMappings
            .groupBy { it.sourceClass.packageName }
            .forEach { (packageName, mappings) ->
                // Use the first source class's file for dependencies
                val sourceFile =
                    mappings.first().sourceClass.containingFile
                        ?: annotatedDeclaration.containingFile!!

                codeGenerator.createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceFile),
                    packageName = packageName,
                    fileName = "CopyMapping__${annotatedDeclaration.underPackageName}",
                ) {
                    it.appendLine("import me.tbsten.cream.*")
                    it.appendLine()

                    mappings.forEach { mapping ->
                        it.appendCopyFunction(
                            source = mapping.sourceClass,
                            target = mapping.targetClass,
                            options = options,
                            omitPackages = listOf("kotlin", packageName.asString()),
                            generateSourceAnnotation =
                                GenerateSourceAnnotation.CopyMapping(
                                    annotationTarget = annotatedDeclaration,
                                    propertyMappings = mapping.propertyMappings,
                                    kdocDescription = mapping.kdocDescription,
                                    kdocExamples = mapping.kdocExamples,
                                ),
                            notCopyToObject = false,
                        )

                        if (mapping.canReverse) {
                            val reversePropertyMappings =
                                mapping.propertyMappings.map { (source, target) ->
                                    target to source
                                }
                            it.appendCopyFunction(
                                source = mapping.targetClass,
                                target = mapping.sourceClass,
                                options = options,
                                omitPackages = listOf("kotlin", packageName.asString()),
                                generateSourceAnnotation =
                                    GenerateSourceAnnotation.CopyMapping(
                                        annotationTarget = annotatedDeclaration,
                                        propertyMappings = reversePropertyMappings,
                                        kdocDescription = mapping.kdocDescription,
                                        kdocExamples = mapping.kdocExamples,
                                    ),
                                notCopyToObject = false,
                            )
                        }
                    }
                }
            }
    }

    return invalidCopyMappingTargets
}
