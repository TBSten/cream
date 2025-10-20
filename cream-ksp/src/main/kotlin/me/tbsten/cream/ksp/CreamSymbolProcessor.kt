package me.tbsten.cream.ksp

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.validate
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CombineMapping
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.options.toCreamOptions
import me.tbsten.cream.ksp.transform.appendCombineToFunction
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.resolveToClassDeclaration
import me.tbsten.cream.ksp.util.underPackageName
import java.io.BufferedWriter

/**
 * Data class to hold CopyMapping annotation information
 *
 * @property sourceClass The source class for the mapping
 * @property targetClass The target class for the mapping
 * @property canReverse Whether bidirectional mapping is enabled
 * @property propertyMappings List of property name mappings (source property name -> target property name)
 */
private data class CopyMappingInfo(
    val sourceClass: KSClassDeclaration,
    val targetClass: KSClassDeclaration,
    val canReverse: Boolean,
    val propertyMappings: List<Pair<String, String>>,
)

/**
 * Data class to hold CombineMapping annotation information
 *
 * @property sourceClasses The source classes to combine from (at least 2 sources)
 * @property targetClass The target class for the mapping
 * @property propertyMappings List of property name mappings (source property name -> target property name)
 */
private data class CombineMappingInfo(
    val sourceClasses: List<KSClassDeclaration>,
    val targetClass: KSClassDeclaration,
    val propertyMappings: List<Pair<String, String>>,
)

class CreamSymbolProcessor(
    options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    private val options = options.toCreamOptions()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val invalidTargets = mutableListOf<KSAnnotated>()

        processCopyFrom(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyTo(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyToChildren(resolver)
            .also { invalidTargets.addAll(it) }

        processCombineTo(resolver)
            .also { invalidTargets.addAll(it) }

        processCombineFrom(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyMapping(resolver)
            .also { invalidTargets.addAll(it) }

        processCombineMapping(resolver)
            .also { invalidTargets.addAll(it) }

        return invalidTargets
    }

    private fun processCopyFrom(resolver: Resolver): List<KSAnnotated> {
        val (copyFromTargets, invalidCopyFromTargets) =
            resolver
                .getSymbolsWithAnnotation(
                    annotationName = CopyFrom::class.fullName,
                ).partition { it.validate() }

        copyFromTargets.forEach { target ->
            val targetDeclaration =
                when (target) {
                    is KSClassDeclaration -> target
                    is KSTypeAlias -> target
                    else ->
                        throw InvalidCreamUsageException(
                            message = "@${CopyFrom::class.simpleName} must be applied to a class, interface, or typealias.",
                            solution = "Please apply @${CopyFrom::class.simpleName} to `class`, `interface`, or `typealias`",
                        )
                }

            val targetClass =
                (targetDeclaration as? KSDeclaration)?.resolveToClassDeclaration()
                    ?: throw InvalidCreamUsageException(
                        message = "@${CopyFrom::class.simpleName} must be applied to a class or interface.",
                        solution = "Please apply @${CopyFrom::class.simpleName} to `class or interface`",
                    )

            // CopyFrom.sources: List<KClass<*>>
            val sourceClasses =
                target
                    .annotations
                    .filter {
                        it.annotationType
                            .resolve()
                            .declaration.fullName == CopyFrom::class.qualifiedName
                    }.flatMap {
                        it.arguments
                            .filter { it.name?.asString() == "sources" }
                            .map { it.value }
                            .filterIsInstance<List<KSType>>()
                            .flatten()
                    }.map { it.declaration }
                    .map { declaration ->
                        declaration.resolveToClassDeclaration()
                            ?: throw InvalidCreamUsageException(
                                message = "${declaration.fullName} (Specified in @${CopyFrom::class.simpleName}.sources of ${target.fullName}) must be class or typealias.",
                                solution = "Specify class, interface, or typealias in @${CopyFrom::class.simpleName}.sources of ${target.fullName}.",
                            )
                    }

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, targetDeclaration.containingFile!!),
                    packageName = targetClass.packageName,
                    fileName = "CopyFrom__${targetClass.underPackageName}",
                ) {
                    it.appendLine("import me.tbsten.cream.*")
                    it.appendLine()

                    sourceClasses.forEach { sourceClass ->
                        it.appendCopyFunction(
                            source = sourceClass,
                            target = targetClass,
                            options = options,
                            omitPackages = listOf("kotlin", targetClass.packageName.asString()),
                            generateSourceAnnotation =
                                GenerateSourceAnnotation.CopyFrom(annotationTarget = targetDeclaration),
                            notCopyToObject = false,
                        )
                    }
                }
        }

        return invalidCopyFromTargets
    }

    private fun processCopyTo(resolver: Resolver): List<KSAnnotated> {
        val (copyToTargets, invalidCopyToTargets) =
            resolver
                .getSymbolsWithAnnotation(
                    annotationName = CopyTo::class.fullName,
                ).partition { it.validate() }

        copyToTargets.forEach { target ->
            val sourceDeclaration =
                when (target) {
                    is KSClassDeclaration -> target
                    is KSTypeAlias -> target
                    else ->
                        throw InvalidCreamUsageException(
                            message = "@${CopyTo::class.simpleName} must be applied to a class, interface, or typealias.",
                            solution = "Please apply @${CopyTo::class.simpleName} to `class`, `interface`, or `typealias`",
                        )
                }

            val sourceClass =
                (sourceDeclaration as? KSDeclaration)?.resolveToClassDeclaration()
                    ?: throw InvalidCreamUsageException(
                        message = "@${CopyTo::class.simpleName} must be applied to a class or interface.",
                        solution = "Please apply @${CopyTo::class.simpleName} to `class or interface`",
                    )

            // CopyTo.targets: List<KClass<*>>
            val targetClasses =
                target
                    .annotations
                    .filter {
                        it.annotationType
                            .resolve()
                            .declaration.fullName == CopyTo::class.qualifiedName
                    }.flatMap {
                        it.arguments
                            .filter { it.name?.asString() == "targets" }
                            .map { it.value }
                            .filterIsInstance<List<KSType>>()
                            .flatten()
                    }.map { it.declaration }
                    .map { declaration ->
                        declaration.resolveToClassDeclaration()
                            ?: throw InvalidCreamUsageException(
                                message = "${declaration.fullName} (Specified in @${CopyTo::class.simpleName}.targets of ${target.fullName}) must be class or typealias.",
                                solution = "Specify class, interface, or typealias in @${CopyTo::class.simpleName}.targets of ${target.fullName}.",
                            )
                    }

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceDeclaration.containingFile!!),
                    packageName = sourceClass.packageName,
                    fileName = "CopyTo__${sourceClass.underPackageName}",
                ) {
                    it.appendLine("import me.tbsten.cream.*")
                    it.appendLine()

                    targetClasses.forEach { targetClass ->
                        // generate sourceClass to sourceClass copy function
                        it.appendCopyFunction(
                            source = sourceClass,
                            target = targetClass,
                            options = options,
                            omitPackages = listOf("kotlin", sourceClass.packageName.asString()),
                            generateSourceAnnotation =
                                GenerateSourceAnnotation.CopyTo(annotationTarget = sourceDeclaration),
                            notCopyToObject = false,
                        )
                    }
                }
        }
        return invalidCopyToTargets
    }

    private fun processCopyToChildren(resolver: Resolver): List<KSAnnotated> {
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
                        throw InvalidCreamUsageException(
                            message = "@${CopyToChildren::class.simpleName} annotation must be applied to a sealed class/interface, but ${copyToChildren.isSealed()}",
                            solution = "",
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

            val targetClasses = sourceSealedClass.getSealedSubclasses()

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
                                GenerateSourceAnnotation.CopyToChildren(annotationTarget = sourceSealedClass),
                            notCopyToObject = notCopyToObject,
                        )
                    }
                }
        }

        return invalidCopyToChildrenTargets
    }

    private class TargetSourcesMapForCombineTo : MutableMap<KSClassDeclaration, MutableList<KSClassDeclaration>> by mutableMapOf() {
        fun put(
            targetClass: KSClassDeclaration,
            sourceClass: KSClassDeclaration,
        ) {
            getOrPut(targetClass) { mutableListOf() }.add(sourceClass)
        }
    }

    private fun processCombineTo(resolver: Resolver): List<KSAnnotated> {
        val (combineToTargets, invalidCombineToTargets) =
            resolver
                .getSymbolsWithAnnotation(
                    annotationName = CombineTo::class.fullName,
                ).partition { it.validate() }

        // Group source classes by their target classes
        val targetToSourcesMap = TargetSourcesMapForCombineTo()

        combineToTargets.forEach { target ->
            val sourceDeclaration =
                when (target) {
                    is KSClassDeclaration -> target
                    is KSTypeAlias -> target
                    else ->
                        throw InvalidCreamUsageException(
                            message = "@${CombineTo::class.simpleName} must be applied to a class, interface, or typealias.",
                            solution = "Please apply @${CombineTo::class.simpleName} to `class`, `interface`, or `typealias`",
                        )
                }

            val sourceClass =
                (sourceDeclaration as? KSDeclaration)?.resolveToClassDeclaration()
                    ?: throw InvalidCreamUsageException(
                        message = "@${CombineTo::class.simpleName} must be applied to a class or interface.",
                        solution = "Please apply @${CombineTo::class.simpleName} to `class or interface`",
                    )

            // CombineTo.targets: List<KClass<*>>
            val targetClasses =
                target
                    .annotations
                    .filter {
                        it.annotationType
                            .resolve()
                            .declaration.fullName == CombineTo::class.qualifiedName
                    }.flatMap {
                        it.arguments
                            .filter { it.name?.asString() == "targets" }
                            .map { it.value }
                            .filterIsInstance<List<KSType>>()
                            .flatten()
                    }.map { it.declaration }
                    .map { declaration ->
                        declaration.resolveToClassDeclaration()
                            ?: throw InvalidCreamUsageException(
                                message = "${declaration.fullName} (Specified in @${CombineTo::class.simpleName}.targets of ${target.fullName}) must be class or typealias.",
                                solution = "Specify class, interface, or typealias in @${CombineTo::class.simpleName}.targets of ${target.fullName}.",
                            )
                    }

            // Group source classes by target class
            targetClasses.forEach { targetClass ->
                targetToSourcesMap.put(targetClass, sourceClass)
            }
        }

        // For each target class, generate copy functions for each source class
        targetToSourcesMap.forEach { (targetClass, sourceClasses) ->
            sourceClasses.forEach { sourceClass ->
                val otherSourceClasses = sourceClasses.filter { it != sourceClass }

                codeGenerator
                    .createNewKotlinFile(
                        dependencies = Dependencies(aggregating = true, sourceClass.containingFile!!),
                        packageName = sourceClass.packageName,
                        fileName = "CombineTo__${sourceClass.underPackageName}__${targetClass.underPackageName}",
                    ) {
                        it.appendLine("import me.tbsten.cream.*")
                        it.appendLine()

                        // Generate combine function with multiple sources
                        it.appendCombineToFunction(
                            primarySource = sourceClass,
                            otherSources = otherSourceClasses,
                            target = targetClass,
                            options = options,
                            omitPackages = listOf("kotlin", sourceClass.packageName.asString()),
                            generateSourceAnnotation =
                                GenerateSourceAnnotation.CombineTo(annotationTarget = sourceClass),
                        )
                    }
            }
        }

        return invalidCombineToTargets
    }

    private fun processCombineFrom(resolver: Resolver): List<KSAnnotated> {
        val (combineFromTargets, invalidCombineFromTargets) =
            resolver
                .getSymbolsWithAnnotation(
                    annotationName = CombineFrom::class.fullName,
                ).partition { it.validate() }

        combineFromTargets.forEach { target ->
            val targetClass =
                (target as? KSClassDeclaration)
                    ?: throw InvalidCreamUsageException(
                        message = "@${CombineFrom::class.simpleName} must be applied to a class or interface.",
                        solution = "Please apply @${CombineFrom::class.simpleName} to `class or interface`",
                    )

            val sourceClasses =
                target
                    .annotations
                    .filter {
                        it.annotationType
                            .resolve()
                            .declaration.fullName == CombineFrom::class.qualifiedName
                    }.flatMap {
                        it.arguments
                            .filter { it.name?.asString() == "sources" }
                            .map { it.value }
                            .filterIsInstance<List<KSType>>()
                            .flatten()
                    }.map { it.declaration }
                    .map {
                        it as? KSClassDeclaration
                            ?: throw InvalidCreamUsageException(
                                message = "${it.fullName} (Specified in @${CombineFrom::class.simpleName}.sources of ${target.fullName}) must be class.",
                                solution = "Specify class or interface in @${CombineFrom::class.simpleName}.sources of ${target.fullName}.",
                            )
                    }.toList()

            // Need at least one source class
            if (sourceClasses.isEmpty()) {
                throw InvalidCreamUsageException(
                    message = "@${CombineFrom::class.simpleName} requires at least one source class.",
                    solution = "Specify at least one source class in @${CombineFrom::class.simpleName}.sources of ${target.fullName}.",
                )
            }

            // First source class is the primary source (extension function receiver)
            val primarySource = sourceClasses.first()
            val otherSources = sourceClasses.drop(1)

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, targetClass.containingFile!!),
                    packageName = targetClass.packageName,
                    fileName = "CombineFrom__${primarySource.underPackageName}__${targetClass.underPackageName}",
                ) {
                    it.appendLine("import me.tbsten.cream.*")
                    it.appendLine()

                    // Generate combine function with multiple sources
                    it.appendCombineToFunction(
                        primarySource = primarySource,
                        otherSources = otherSources,
                        target = targetClass,
                        options = options,
                        omitPackages = listOf("kotlin", primarySource.packageName.asString()),
                        generateSourceAnnotation =
                            GenerateSourceAnnotation.CombineFrom(annotationTarget = targetClass),
                    )
                }
        }

        return invalidCombineFromTargets
    }

    private fun processCopyMapping(resolver: Resolver): List<KSAnnotated> {
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

                        CopyMappingInfo(sourceClass, targetClass, canReverse, propertyMaps)
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

    private fun processCombineMapping(resolver: Resolver): List<KSAnnotated> {
        val (combineMappingTargets, invalidCombineMappingTargets) =
            resolver
                .getSymbolsWithAnnotation(
                    annotationName = CombineMapping::class.fullName,
                ).partition { it.validate() }

        combineMappingTargets.forEach { target ->
            val annotatedDeclaration =
                (target as? KSClassDeclaration)
                    ?: throw InvalidCreamUsageException(
                        message = "@${CombineMapping::class.simpleName} must be applied to a class.",
                        solution = "Please apply @${CombineMapping::class.simpleName} to a `class` or `object`",
                    )

            // Extract all CombineMapping annotations from the target
            val combineMappings =
                target
                    .annotations
                    .filter {
                        it.annotationType
                            .resolve()
                            .declaration.fullName == CombineMapping::class.qualifiedName
                    }.map { annotation ->
                        val sourcesTypes =
                            annotation.arguments
                                .firstOrNull { it.name?.asString() == "sources" }
                                ?.value as? List<*>
                                ?: throw InvalidCreamUsageException(
                                    message = "sources parameter is required in @${CombineMapping::class.simpleName}",
                                    solution = "Specify at least 2 source classes in @${CombineMapping::class.simpleName}",
                                )

                        val targetType =
                            annotation.arguments
                                .firstOrNull { it.name?.asString() == "target" }
                                ?.value as? KSType
                                ?: throw InvalidCreamUsageException(
                                    message = "target parameter is required in @${CombineMapping::class.simpleName}",
                                    solution = "Specify target class in @${CombineMapping::class.simpleName}",
                                )

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

                        val sourceClasses =
                            sourcesTypes.mapNotNull { sourceType ->
                                (sourceType as? KSType)?.declaration as? KSClassDeclaration
                            }

                        if (sourceClasses.size < 2) {
                            throw InvalidCreamUsageException(
                                message = "@${CombineMapping::class.simpleName} requires at least 2 source classes, but got ${sourceClasses.size}.",
                                solution = "Specify at least 2 source classes in @${CombineMapping::class.simpleName}.sources",
                            )
                        }

                        sourceClasses.forEach { sourceClass ->
                            if (sourceClass.classKind != ClassKind.CLASS &&
                                sourceClass.classKind != ClassKind.ANNOTATION_CLASS
                            ) {
                                throw InvalidCreamUsageException(
                                    message = "${sourceClass.fullName} (Specified in @${CombineMapping::class.simpleName}.sources) must be a class.",
                                    solution = "Specify a class in @${CombineMapping::class.simpleName}.sources",
                                )
                            }
                        }

                        val targetClass =
                            targetType.declaration as? KSClassDeclaration
                                ?: throw InvalidCreamUsageException(
                                    message = "${targetType.declaration.fullName} (Specified in @${CombineMapping::class.simpleName}.target) must be a class.",
                                    solution = "Specify a class in @${CombineMapping::class.simpleName}.target",
                                )

                        CombineMappingInfo(sourceClasses, targetClass, propertyMaps)
                    }

            // Group by first source class package to create one file per source package
            combineMappings
                .groupBy { it.sourceClasses.first().packageName }
                .forEach { (packageName, mappings) ->
                    // Use the first source class's file for dependencies
                    val sourceFile =
                        mappings
                            .first()
                            .sourceClasses
                            .first()
                            .containingFile
                            ?: annotatedDeclaration.containingFile!!

                    codeGenerator.createNewKotlinFile(
                        dependencies = Dependencies(aggregating = true, sourceFile),
                        packageName = packageName,
                        fileName = "CombineMapping__${annotatedDeclaration.underPackageName}",
                    ) {
                        it.appendLine("import me.tbsten.cream.*")
                        it.appendLine()

                        mappings.forEach { mapping ->
                            val primarySource = mapping.sourceClasses.first()
                            val otherSources = mapping.sourceClasses.drop(1)

                            it.appendCombineToFunction(
                                primarySource = primarySource,
                                otherSources = otherSources,
                                target = mapping.targetClass,
                                options = options,
                                omitPackages = listOf("kotlin", packageName.asString()),
                                generateSourceAnnotation =
                                    GenerateSourceAnnotation.CombineMapping(
                                        annotationTarget = annotatedDeclaration,
                                        propertyMappings = mapping.propertyMappings,
                                    ),
                            )
                        }
                    }
                }
        }

        return invalidCombineMappingTargets
    }
}

private fun CodeGenerator.createNewKotlinFile(
    dependencies: Dependencies,
    packageName: KSName,
    fileName: String,
    block: (BufferedWriter) -> Unit,
) = createNewFile(
    dependencies = dependencies,
    packageName = packageName.asString(),
    fileName = fileName,
).bufferedWriter()
    .use {
        it.appendLine("package ${packageName.asString()}")
        it.appendLine()

        block(it)
    }
