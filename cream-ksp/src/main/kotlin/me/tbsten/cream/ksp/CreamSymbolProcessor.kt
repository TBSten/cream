package me.tbsten.cream.ksp

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import me.tbsten.cream.CopyFrom
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

        return invalidTargets
    }

    private fun processCopyFrom(resolver: Resolver): List<KSAnnotated> {
        val (copyFromTargets, invalidCopyFromTargets) = resolver.getSymbolsWithAnnotation(
            annotationName = CopyFrom::class.qualifiedName!!,
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
            annotationName = CopyTo::class.qualifiedName!!,
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
            annotationName = CopyToChildren::class.qualifiedName!!,
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
            val copyToChildrenAnnotation =
                sourceSealedClass.getAnnotationsByType(CopyToChildren::class).firstOrNull()
            val notCopyToObject = copyToChildrenAnnotation
                ?.notCopyToObject
                ?: options.notCopyToObject

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
