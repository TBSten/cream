package me.tbsten.cream.ksp

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.options.toCreamOptions
import me.tbsten.cream.ksp.transform.appendCopyFunction
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.isSealed
import me.tbsten.cream.ksp.util.underPackageName
import java.io.BufferedWriter

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

        processCopyMapping(resolver)
            .also { invalidTargets.addAll(it) }

        return invalidTargets
    }

    private fun processCopyFrom(resolver: Resolver): List<KSAnnotated> {
        val (copyFromTargets, invalidCopyFromTargets) = resolver.getSymbolsWithAnnotation(
            annotationName = CopyFrom::class.fullName,
        ).partition { it.validate() }

        copyFromTargets.forEach { target ->
            val targetClass = (target as? KSClassDeclaration)
                ?: throw InvalidCreamUsageException(
                    message = "@${CopyFrom::class.simpleName} must be applied to a class or interface.",
                    solution = "Please apply @${CopyFrom::class.simpleName} to `class or interface`",
                )

            // CopyFrom.sources: List<KClass<*>>
            val sourceClasses = target
                .annotations
                .filter { it.annotationType.resolve().declaration.fullName == CopyFrom::class.qualifiedName }
                .flatMap {
                    it.arguments
                        .filter { it.name?.asString() == "sources" }
                        .map { it.value }
                        .filterIsInstance<List<KSType>>()
                        .flatten()
                }.map { it.declaration }
                .map {
                    it as? KSClassDeclaration
                        ?: throw InvalidCreamUsageException(
                            message = "${it.fullName} (Specified in @${CopyFrom::class.simpleName}.sources of ${target.fullName}) must be class.",
                            solution = "Specify class or interface in @${CopyFrom::class.simpleName}.sources of ${target.fullName}.",
                        )
                }

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, targetClass.containingFile!!),
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
                                GenerateSourceAnnotation.CopyFrom(annotationTarget = target),
                            notCopyToObject = false,
                        )
                    }
                }
        }

        return invalidCopyFromTargets
    }

    private fun processCopyTo(resolver: Resolver): List<KSAnnotated> {
        val (copyToTargets, invalidCopyToTargets) = resolver.getSymbolsWithAnnotation(
            annotationName = CopyTo::class.fullName,
        ).partition { it.validate() }

        copyToTargets.forEach { target ->
            val sourceClass = (target as? KSClassDeclaration)
                ?: throw InvalidCreamUsageException(
                    message = "@${CopyTo::class.simpleName} must be applied to a class or interface.",
                    solution = "Please apply @${CopyTo::class.simpleName} to `class or interface`",
                )

            // CopyTo.targets: List<KClass<*>>
            val targetClasses = target
                .annotations
                .filter { it.annotationType.resolve().declaration.fullName == CopyTo::class.qualifiedName }
                .flatMap {
                    it.arguments
                        .filter { it.name?.asString() == "targets" }
                        .map { it.value }
                        .filterIsInstance<List<KSType>>()
                        .flatten()
                }.map { it.declaration }
                .map {
                    it as? KSClassDeclaration
                        ?: throw InvalidCreamUsageException(
                            message = "${it.fullName} (Specified in @${CopyTo::class.simpleName}.sources of ${target.fullName}) must be class.",
                            solution = "Specify class or interface in @${CopyTo::class.simpleName}.sources of ${target.fullName}.",
                        )
                }

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceClass.containingFile!!),
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
                                GenerateSourceAnnotation.CopyTo(annotationTarget = target),
                            notCopyToObject = false,
                        )
                    }
                }
        }
        return invalidCopyToTargets
    }

    private fun processCopyToChildren(resolver: Resolver): List<KSAnnotated> {
        val (copyToChildrenTargets, invalidCopyToChildrenTargets) = resolver.getSymbolsWithAnnotation(
            annotationName = CopyToChildren::class.fullName,
        ).partition { it.validate() }

        copyToChildrenTargets.forEach { copyToChildren ->
            val sourceSealedClass = run {
                if (copyToChildren !is KSClassDeclaration)
                    throw InvalidCreamUsageException(
                        message =
                            "@${CopyToChildren::class.simpleName} annotation must be applied to a sealed class/interface."
                                    + if (copyToChildren is KSDeclaration) copyToChildren.simpleName.asString() + " is not sealed class/interface" else "",
                        solution = (copyToChildren as? KSDeclaration)?.let { "Make ${it.fullName} a sealed class/interface." },
                    )

                if (!copyToChildren.isSealed())
                    throw InvalidCreamUsageException(
                        message = "@${CopyToChildren::class.simpleName} annotation must be applied to a sealed class/interface, but ${copyToChildren.isSealed()}",
                        solution = "",
                    )

                copyToChildren
            }
            // Enclose notCopyToObject in runCatching because it may cause an error if notCopyToObject cannot be obtained.
            val notCopyToObject = runCatching {
                val copyToChildrenAnnotation =
                    sourceSealedClass.getAnnotationsByType(CopyToChildren::class).firstOrNull()
                copyToChildrenAnnotation
                    ?.notCopyToObject
                    ?: options.notCopyToObject
            }.getOrDefault(false)

            val targetClasses = sourceSealedClass.getSealedSubclasses()

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(
                        aggregating = true,
                        sourceSealedClass.containingFile!!
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
                            omitPackages = listOf(
                                "kotlin",
                                sourceSealedClass.packageName.asString()
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

    private fun processCopyMapping(resolver: Resolver): List<KSAnnotated> {
        val (copyMappingTargets, invalidCopyMappingTargets) = resolver.getSymbolsWithAnnotation(
            annotationName = CopyMapping::class.fullName,
        ).partition { it.validate() }

        copyMappingTargets.forEach { target ->
            val annotatedDeclaration = (target as? KSClassDeclaration)
                ?: throw InvalidCreamUsageException(
                    message = "@${CopyMapping::class.simpleName} must be applied to a class.",
                    solution = "Please apply @${CopyMapping::class.simpleName} to a `class` or `object`",
                )

            // Extract all CopyMapping annotations from the target
            val copyMappings = target
                .annotations
                .filter { it.annotationType.resolve().declaration.fullName == CopyMapping::class.qualifiedName }
                .map { annotation ->
                    val sourceType = annotation.arguments
                        .firstOrNull { it.name?.asString() == "source" }
                        ?.value as? KSType
                        ?: throw InvalidCreamUsageException(
                            message = "source parameter is required in @${CopyMapping::class.simpleName}",
                            solution = "Specify source class in @${CopyMapping::class.simpleName}",
                        )

                    val targetType = annotation.arguments
                        .firstOrNull { it.name?.asString() == "target" }
                        ?.value as? KSType
                        ?: throw InvalidCreamUsageException(
                            message = "target parameter is required in @${CopyMapping::class.simpleName}",
                            solution = "Specify target class in @${CopyMapping::class.simpleName}",
                        )

                    val canReverse = annotation.arguments
                        .firstOrNull { it.name?.asString() == "canReverse" }
                        ?.value as? Boolean
                        ?: false

                    val sourceClass = sourceType.declaration as? KSClassDeclaration
                        ?: throw InvalidCreamUsageException(
                            message = "${sourceType.declaration.fullName} (Specified in @${CopyMapping::class.simpleName}.source) must be a class.",
                            solution = "Specify a class in @${CopyMapping::class.simpleName}.source",
                        )

                    val targetClass = targetType.declaration as? KSClassDeclaration
                        ?: throw InvalidCreamUsageException(
                            message = "${targetType.declaration.fullName} (Specified in @${CopyMapping::class.simpleName}.target) must be a class.",
                            solution = "Specify a class in @${CopyMapping::class.simpleName}.target",
                        )

                    Triple(sourceClass, targetClass, canReverse)
                }

            // Group by source class to create one file per source package
            copyMappings.groupBy { (sourceClass, _, _) -> sourceClass.packageName }
                .forEach { (packageName, mappings) ->
                    // Use the first source class's file for dependencies
                    val sourceFile = mappings.first().first.containingFile
                        ?: annotatedDeclaration.containingFile!!

                    codeGenerator.createNewKotlinFile(
                        dependencies = Dependencies(aggregating = true, sourceFile),
                        packageName = packageName,
                        fileName = "CopyMapping__${annotatedDeclaration.underPackageName}",
                    ) {
                        it.appendLine("import me.tbsten.cream.*")
                        it.appendLine()

                        mappings.forEach { (sourceClass, targetClass, canReverse) ->
                            // Generate forward copy function (source -> target)
                            it.appendCopyFunction(
                                source = sourceClass,
                                target = targetClass,
                                options = options,
                                omitPackages = listOf("kotlin", packageName.asString()),
                                generateSourceAnnotation =
                                    GenerateSourceAnnotation.CopyMapping(annotationTarget = annotatedDeclaration),
                                notCopyToObject = false,
                            )

                            // Generate reverse copy function (target -> source) if canReverse is true
                            if (canReverse) {
                                it.appendCopyFunction(
                                    source = targetClass,
                                    target = sourceClass,
                                    options = options,
                                    omitPackages = listOf("kotlin", packageName.asString()),
                                    generateSourceAnnotation =
                                        GenerateSourceAnnotation.CopyMapping(annotationTarget = annotatedDeclaration),
                                    notCopyToObject = false,
                                )
                            }
                        }
                    }
                }
        }

        return invalidCopyMappingTargets
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
)
    .bufferedWriter()
    .use {
        it.appendLine("package ${packageName.asString()}")
        it.appendLine()

        block(it)
    }
