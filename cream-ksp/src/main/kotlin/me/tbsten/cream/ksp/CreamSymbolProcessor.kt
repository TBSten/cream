package me.tbsten.cream.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
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
                ?: error("Expected a class declaration with @CopyFrom annotation, but found: $target")

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
                        ?: error("${it.fullName} is not class.")
                }

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, targetClass.containingFile!!),
                    packageName = targetClass.packageName,
                    fileName = "CopyFrom__${targetClass.simpleName.asString()}",
                ) {
                    sourceClasses.forEach { sourceClass ->
                        it.appendCopyFunction(
                            source = sourceClass,
                            target = targetClass,
                            options = options,
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
                ?: error("Expected a class declaration with @CopyTo annotation, but found: $target")

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
                        ?: error("${it.fullName} must be class.")
                }

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceClass.containingFile!!),
                    packageName = sourceClass.packageName,
                    fileName = "CopyTo__${sourceClass.simpleName.asString()}",
                ) {
                    targetClasses.forEach { targetClass ->
                        // generate sourceClass to sourceClass copy function
                        it.appendCopyFunction(
                            source = sourceClass,
                            target = targetClass,
                            options = options,
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
            val sourceClass = (copyToChildren as? KSClassDeclaration)
                ?.takeIf { it.isSealed() }
                ?: error("Expected a sealed class/interface declaration with @CopyToChildren annotation, but found: $copyToChildren")

            val targetClasses = sourceClass.getSealedSubclasses()

            codeGenerator
                .createNewKotlinFile(
                    dependencies = Dependencies(aggregating = true, sourceClass.containingFile!!),
                    packageName = sourceClass.packageName,
                    fileName = "CopyToChildren__${sourceClass.simpleName.asString()}",
                ) {
                    targetClasses.forEach { targetClass ->
                        // generate sourceClass to sourceClass copy function
                        it.appendCopyFunction(
                            source = sourceClass,
                            target = targetClass,
                            options = options,
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
